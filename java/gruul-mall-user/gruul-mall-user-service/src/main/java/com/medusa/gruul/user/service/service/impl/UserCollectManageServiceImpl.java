package com.medusa.gruul.user.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.medusa.gruul.cart.api.rpc.CartRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.page.PageBean;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;
import com.medusa.gruul.user.api.model.UserCollectVO;
import com.medusa.gruul.user.service.model.dto.CollectDTO;
import com.medusa.gruul.user.service.model.dto.UserCollectDTO;
import com.medusa.gruul.user.service.model.dto.UserCollectQueryDTO;
import com.medusa.gruul.user.service.mp.entity.UserCollect;
import com.medusa.gruul.user.service.mp.mapper.UserCollectMapper;
import com.medusa.gruul.user.service.mp.service.IUserCollectService;
import com.medusa.gruul.user.service.service.UserCollectManageService;
import io.vavr.control.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 用户收藏 服务实现类
 *
 * @author WuDi date 2022/9/20
 */
@Service
@RequiredArgsConstructor
public class UserCollectManageServiceImpl implements UserCollectManageService {





    private final IUserCollectService userCollectService;

    private final UserCollectMapper userCollectMapper;

    private final CartRpcService cartRpcService;

    private OrderRpcService orderRpcService;

    private GoodsRpcService goodsRpcService;

    private  StorageRpcService storageRpcService;


    @Lazy
    @Autowired
    public void setOrderRpcService(OrderRpcService orderRpcService) {
        this.orderRpcService = orderRpcService;
    }

    @Lazy
    @Autowired
    public void setGoodsRpcService(GoodsRpcService goodsRpcService) {
        this.goodsRpcService = goodsRpcService;
    }

    @Lazy
    @Autowired
    public void setStorageRpcService(StorageRpcService storageRpcService) {
        this.storageRpcService = storageRpcService;
    }

    /**
     * 用户收藏、取消收藏
     *
     * @param collectDTO 用户收藏
     */
    @Override
    public void userCollect(CollectDTO collectDTO) {
        Long userId = ISecurity.userMust().getId();
        //添加收藏
        if (collectDTO.getWhetherCollect()) {
            addUserCollect(userId, collectDTO.getUserCollectDTOList());
        } else {
            //取消收藏
            batchCancelUserCollect(userId, collectDTO.getUserCollectDTOList());
        }
    }

    /**
     * 取消收藏
     *
     * @param userId       用户id
     * @param userCollects 取消收藏商品数组
     */
    private void batchCancelUserCollect(Long userId, List<UserCollectDTO> userCollects) {
        LambdaUpdateChainWrapper<UserCollect> userCollectUpdate = userCollectService.lambdaUpdate()
                .eq(UserCollect::getUserId, userId);
        userCollectUpdate.and(
                updateWrapper ->
                        userCollects.forEach(
                                collect ->
                                        updateWrapper.or(
                                                innerUw -> innerUw.eq(UserCollect::getProductId, collect.getProductId())
                                                        .eq(UserCollect::getShopId, collect.getShopId())
                                        )
                        )
        );
        boolean success = userCollectUpdate.remove();
        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
    }

    private void addUserCollect(Long userId, List<UserCollectDTO> userCollects) {
        //将收藏的商品数据信息封装到map中
        Map<Long, List<Long>> shopProdutIdsMap = userCollects.stream()
            .collect(Collectors.toMap(
                UserCollectDTO::getShopId,
                userCollect -> new ArrayList<>(Collections.singletonList(userCollect.getProductId())),
                (existingList, newList) -> {
                    existingList.addAll(newList);
                    return existingList;
                },
                HashMap::new
            ));


        // 循环查询数据库，改为or拼接，限制 userCollects size 最长 1000
        LambdaQueryChainWrapper<UserCollect> userCollectQuery = userCollectService.lambdaQuery()
                .eq(UserCollect::getUserId, userId);
        userCollectQuery.and(
                queryWrapper ->
                        userCollects.forEach(
                                userCollectDto ->
                                        queryWrapper.or(
                                                innerQw -> innerQw.eq(UserCollect::getProductId,
                                                                userCollectDto.getProductId())
                                                        .eq(UserCollect::getShopId, userCollectDto.getShopId())
                                        )
                        )
        );
        List<UserCollect> userCollectList = userCollectQuery.list();
        if (!CollUtil.isEmpty(userCollectList)) {
            //收藏只有一条的情况下，数据才有可能变更，超过一条数据，也是这个表里查询出来的，没必要更新
            if (userCollects.size() == CommonPool.NUMBER_ONE) {
                UserCollectDTO userCollectDTO = userCollects.get(CommonPool.NUMBER_ZERO);
                userCollectService.lambdaUpdate()
                        .set(UserCollect::getProductName, userCollectDTO.getProductName())
                        .set(UserCollect::getProductPic, userCollectDTO.getProductPic())
                        .set(UserCollect::getProductPrice, userCollectDTO.getProductPrice())
                        .eq(UserCollect::getUserId, userId)
                        .eq(UserCollect::getProductId, userCollectDTO.getProductId())
                        .eq(UserCollect::getShopId, userCollectDTO.getShopId())
                        .update();
                cartRpcService.removeProductFromCart(userId, shopProdutIdsMap);
                return;
            }
        }

        Map<ShopProductKeyDTO, List<UserCollect>> userCollectMap = userCollectList.stream()
                .collect(Collectors.groupingBy(
                        userCollect -> new ShopProductKeyDTO()
                                .setShopId(userCollect.getShopId())
                                .setProductId(userCollect.getProductId()))
                );

        // 判断不存在的，拼装成 UserCollect 数组，进行批量插入操作
        List<UserCollect> newUserCollect = userCollects.stream().filter(userCollectDTO ->
                CollUtil.isEmpty(
                        userCollectMap.get(
                                new ShopProductKeyDTO()
                                        .setShopId(userCollectDTO.getShopId())
                                        .setProductId(userCollectDTO.getProductId()))
                )
        ).map(userCollectDTO ->
                new UserCollect().setUserId(userId)
                        .setShopId(userCollectDTO.getShopId())
                        .setProductId(userCollectDTO.getProductId())
                        .setProductPic(userCollectDTO.getProductPic())
                        .setProductName(userCollectDTO.getProductName())
                        .setSupplierId(userCollectDTO.getSupplierId())
                        .setProductPrice(userCollectDTO.getProductPrice())
        ).toList();

        //批量新增
        if (!CollUtil.isEmpty(newUserCollect)) {
            boolean saveBatch = userCollectService.saveBatch(newUserCollect);
            SystemCode.DATA_ADD_FAILED.falseThrow(saveBatch);
        }
        cartRpcService.removeProductFromCart(userId, shopProdutIdsMap);
    }

    /**
     * 用户取消收藏
     *
     * @param shopId    店铺id
     * @param productId 产品id
     */
    @Override
    public void cancelUserCollect(Long shopId, Long productId) {
        Long userId = ISecurity.userOpt().get().getId();
        boolean success = userCollectService.lambdaUpdate()
                .eq(UserCollect::getUserId, userId)
                .eq(UserCollect::getShopId, shopId)
                .eq(UserCollect::getProductId, productId)
                .remove();
        if (!success) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED);
        }
    }

    @Override
    public IPage<UserCollectVO> getUserCollectPage(UserCollectQueryDTO userCollectQuery) {

        Page<UserCollect> page = userCollectService.lambdaQuery()
                .like(StrUtil.isNotBlank(userCollectQuery.getProductName()), UserCollect::getProductName,
                        userCollectQuery.getProductName())
                .eq(UserCollect::getUserId, ISecurity.userOpt().get().getId())
                .page(userCollectQuery);
        List<UserCollect> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        }
        Map<Long, Long> evaluatePerson = orderRpcService.getEvaluatePerson(
                records.stream().map(UserCollect::getProductId).collect(Collectors.toSet()));

        List<ShopProductKeyDTO> shopProductKeys = records.stream()
                .map(record -> new ShopProductKeyDTO().setShopId(
                                record.getSupplierId() == null ? record.getShopId() : record.getSupplierId())
                        .setProductId(record.getProductId()))
                .toList();
        Map<String, ProductStatisticsVO> productStatisticsMap = storageRpcService.getProductStatisticsMap(
                new ArrayList<>(shopProductKeys));
        List<UserCollectVO> recordList = records.stream()
                .map(
                        userCollect -> {
                            UserCollectVO userCollectVO = new UserCollectVO();
                            userCollectVO.setSalesVolume(
                                    Option.of(productStatisticsMap.get(RedisUtil.key(
                                                    userCollect.getSupplierId() == null ? userCollect.getShopId()
                                                            : userCollect.getSupplierId(), userCollect.getProductId())))
                                            .map(ProductStatisticsVO::getSalesVolume)
                                            .getOrElse(0L)
                            );
                            userCollectVO.setProductId(userCollect.getProductId())
                                    .setShopId(userCollect.getShopId())
                                    .setProductName(userCollect.getProductName())
                                    .setProductPrice(userCollect.getProductPrice())
                                    .setProductPic(userCollect.getProductPic())
                                    .setStatus(ProductStatus.SELL_OFF);
                            // 远程查询商品信息
                            Product productInfo = goodsRpcService.getProductInfo(userCollect.getShopId(),
                                    userCollect.getProductId());
                            if (productInfo != null) {
                                userCollectVO.setProductPic(productInfo.getPic())
                                        .setEvaluatePerson(Option.of(evaluatePerson.get(userCollect.getProductId()))
                                                .getOrElse(Long.valueOf(CommonPool.NUMBER_ZERO)))
                                        .setProductPrice(
                                                productInfo.getSalePrices() != null ? productInfo.getSalePrices().get(0)
                                                        : 0L)
                                        .setStatus(productInfo.getStatus());
                            }

                            return userCollectVO;
                        }
                ).collect(Collectors.toList());
        return PageBean.getPage(userCollectQuery, recordList, page.getTotal(), page.getPages());

    }


    /**
     * 查询用户是否对该商品进行收藏
     *
     * @param shopId    店铺id
     * @param productId 产品id
     * @return 返回结果
     */
    @Override
    public Boolean findUserIsCollect(Long shopId, Long productId, Long userId) {
        return userCollectService.lambdaQuery()
                .eq(UserCollect::getUserId, userId)
                .eq(UserCollect::getShopId, shopId)
                .eq(UserCollect::getProductId, productId)
                .exists();
    }

    /**
     * 查询收藏量最多的商品
     *
     * @param shopId 店铺id
     * @return 商品id
     */
    @Override
    public List<Long> getShopProductCollection(Long shopId) {
        List<UserCollectVO> userCollectVOList = userCollectMapper.getShopProductCollection(shopId);
        if (CollectionUtil.isEmpty(userCollectVOList)) {
            return null;
        }
        return userCollectVOList.stream().map(UserCollectVO::getProductId).collect(Collectors.toList());
    }

    /**
     * 用户收藏数量包含商品与店铺
     *
     * @param userId 用户userId
     * @return 收藏数量
     */
    @Override
    public Long getUserCollectInfoCount(Long userId) {
        Long goodsCollectCount = userCollectService.lambdaQuery()
                .eq(UserCollect::getUserId, userId)
                .count();
        return goodsCollectCount + goodsRpcService.shopFollow(userId);
    }
}


