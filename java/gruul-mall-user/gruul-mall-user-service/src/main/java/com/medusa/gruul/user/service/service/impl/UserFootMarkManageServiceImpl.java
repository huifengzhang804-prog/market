package com.medusa.gruul.user.service.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.page.PageBean;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ConsignmentPriceSettingDTO;
import com.medusa.gruul.goods.api.model.enums.PricingType;
import com.medusa.gruul.goods.api.model.param.PlatformCategoryParam;
import com.medusa.gruul.goods.api.model.param.ProductRandomParam;
import com.medusa.gruul.goods.api.model.vo.ApiPlatformProductVO;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;
import com.medusa.gruul.user.api.constant.UserConstant;
import com.medusa.gruul.user.api.enums.UserFootMarkStatus;
import com.medusa.gruul.user.api.enums.UserRabbit;
import com.medusa.gruul.user.api.model.GuessYouLikeVO;
import com.medusa.gruul.user.api.model.UserFootMarkVO;
import com.medusa.gruul.user.api.model.dto.UserFootMarkDTO;
import com.medusa.gruul.user.service.model.dto.GuessYouLikeQueryDTO;
import com.medusa.gruul.user.service.model.dto.UserFootMarkQueryDTO;
import com.medusa.gruul.user.service.mp.entity.UserFootMark;
import com.medusa.gruul.user.service.mp.mapper.UserFootMarkMapper;
import com.medusa.gruul.user.service.mp.service.IUserFootMarkService;
import com.medusa.gruul.user.service.service.UserFootMarkManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author WuDi
 * @since 2022/9/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserFootMarkManageServiceImpl implements UserFootMarkManageService {

    private final Executor globalExecutor;
    private  GoodsRpcService goodsRpcService;
    private  StorageRpcService storageRpcService;
    private  OrderRpcService orderRpcService;
    private final RabbitTemplate rabbitTemplate;
    private final UserFootMarkMapper userFootMarkMapper;
    private final IUserFootMarkService userFootMarkService;

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

    @Lazy
    @Autowired
    public void setOrderRpcService(OrderRpcService orderRpcService) {
        this.orderRpcService = orderRpcService;
    }


    /**
     * 获取用户足迹
     *
     * @param userFootMarkQuery 用户足迹查询条件
     * @return 用户足迹
     */
    @Override
    public IPage<UserFootMarkVO> userFootMarkPage(UserFootMarkQueryDTO userFootMarkQuery) {
        Long userId = ISecurity.userOpt().get().getId();
        userFootMarkQuery.setUserId(userId);
        IPage<UserFootMarkVO> userFootMarkPage = userFootMarkMapper.userFootMarkPage(userFootMarkQuery);
        List<UserFootMarkVO> userFootMarkPageRecords = userFootMarkPage.getRecords();
        if (CollectionUtil.isEmpty(userFootMarkPageRecords)) {
            return userFootMarkPage;
        }
        Set<Long> productIdSet = userFootMarkPageRecords.stream().map(UserFootMarkVO::getProductId).collect(Collectors.toSet());
        Map<Long, Long> evaluatePersonMap = orderRpcService.getEvaluatePerson(productIdSet);
        userFootMarkPageRecords.forEach(userFootMarkVO -> {
            userFootMarkVO.setEvaluatePerson(evaluatePersonMap.get(userFootMarkVO.getProductId()));
            // 远程查询商品信息
            Product productInfo = goodsRpcService.getProductInfo(userFootMarkVO.getShopId(), userFootMarkVO.getProductId());
            if (productInfo != null) {
                userFootMarkVO.setStatus(productInfo.getStatus())
                        .setProductName(productInfo.getName())
                        .setProductPrice(productInfo.getSalePrices() != null ? productInfo.getSalePrices().get(0) : null)
                        .setProductPic(productInfo.getPic());
            }
        });
        return userFootMarkPage;
    }


    /**
     * 添加用户足迹
     *
     * @param footMark 用户足迹
     */
    @Override
    public void addUserFootMark(UserFootMarkDTO footMark, Long userId) {
        //异步加锁执行
        globalExecutor.execute(
                () -> {
                    Long productId = footMark.getProductId();
                    Long shopId = footMark.getShopId();
                    //加锁后执行
                    RedisUtil.lockRun(
                            RedisUtil.getLockKey(
                                    RedisUtil.key(UserConstant.USER_FOOT_MARK_SAVE_LOCK, userId, shopId, productId)
                            ),
                            () -> {
                                LocalDate date = LocalDate.now();
                                UserFootMark useFootMark = userFootMarkService.lambdaQuery()
                                        .select(UserFootMark::getId)
                                        .eq(UserFootMark::getBrowsDate, date)
                                        .eq(UserFootMark::getUserId, userId)
                                        .eq(UserFootMark::getShopId, shopId)
                                        .eq(UserFootMark::getProductId, productId)
                                        .one();
                                if (useFootMark != null) {
                                    userFootMarkService.lambdaUpdate()
                                            .set(UserFootMark::getUpdateTime, LocalDateTime.now())
                                            .set(UserFootMark::getProductPic, footMark.getProductPic())
                                            .set(UserFootMark::getProductName, footMark.getProductName())
                                            .set(UserFootMark::getProductPrice, footMark.getProductPrice())
                                            .set(UserFootMark::getPlatformCategoryId, footMark.getPlatformCategoryId())
                                            .eq(UserFootMark::getBrowsDate, date)
                                            .eq(UserFootMark::getUserId, userId)
                                            .eq(UserFootMark::getShopId, shopId)
                                            .eq(UserFootMark::getProductId, productId)
                                            .update();
                                    return;
                                }
                                userFootMarkService.save(
                                        new UserFootMark()
                                                .setUserId(userId)
                                                .setShopId(shopId)
                                                .setProductId(productId)
                                                .setProductPic(footMark.getProductPic())
                                                .setProductName(footMark.getProductName())
                                                .setProductPrice(footMark.getProductPrice())
                                                .setBrowsDate(date)
                                                .setPlatformCategoryId(footMark.getPlatformCategoryId())
                                );
                                // 用户足迹新增广播
                                rabbitTemplate.convertAndSend(
                                        UserRabbit.USER_FOOTPRINT_ADD.exchange(),
                                        UserRabbit.USER_FOOTPRINT_ADD.routingKey(),
                                        JSONObject.of("shopId", shopId)
                                                .fluentPut("userId", userId)
                                );
                            }
                    );
                }
        );
    }


    /**
     * 足迹批量删除
     *
     * @param userFootMarkIds    足迹id
     * @param userFootMarkStatus 足迹枚举
     */
    @Override
    public void batchDeleteUserFootMark(Set<Long> userFootMarkIds, UserFootMarkStatus userFootMarkStatus) {
        Long userId = ISecurity.userMust().getId();
        if (UserFootMarkStatus.EMPTY == userFootMarkStatus) {
            boolean remove = userFootMarkService.lambdaUpdate()
                    .eq(UserFootMark::getUserId, userId)
                    .remove();
            if (!remove) {
                throw new GlobalException(SystemCode.DATA_DELETE_FAILED_CODE, "清空足迹失败");
            }
            return;
        }

        boolean success = userFootMarkService.removeByIds(userFootMarkIds);
        if (!success) {
            throw new GlobalException(SystemCode.DATA_DELETE_FAILED_CODE, "删除足迹失败");
        }

    }

    /**
     * 根据月份获取有足迹的日期
     *
     * @param month 月份
     * @return 有足迹的日期
     */
    @Override
    public List<LocalDate> getFootMarkBrowsDates(Integer month) {
        return userFootMarkService.lambdaQuery()
                .select(UserFootMark::getBrowsDate)
                .eq(UserFootMark::getUserId, ISecurity.userOpt().get().getId())
                .apply("month(brows_date)=" + month)
                .groupBy(UserFootMark::getBrowsDate)
                .list().stream().map(UserFootMark::getBrowsDate)
                .collect(Collectors.toList());
    }

    /**
     * 猜你喜欢
     * todo 代码太乱了 需要优化下
     *
     * @param guessYouLikeQuery 猜你喜欢查询参数
     * @return 猜你喜欢VO
     */
    @Override
    public IPage<GuessYouLikeVO> guessYouLike(GuessYouLikeQueryDTO guessYouLikeQuery) {
        Long userId = ISecurity.userOpt().map(SecureUser::getId).getOrNull();
        List<GuessYouLikeVO> guessYouLikeVoList = new ArrayList<>();
        if (userId != null) {
            // 分组查询最新的3个平台三级类目id
            List<Long> platformCategoryList = userFootMarkMapper.selectThreePlatformCategoryId(userId).stream().map(UserFootMark::getPlatformCategoryId).collect(Collectors.toList());
            platformCategoryList.removeAll(Collections.singleton(null));
            if (CollectionUtil.isNotEmpty(platformCategoryList)) {
                PlatformCategoryParam platformCategoryParam = new PlatformCategoryParam();
                platformCategoryParam.setSize(guessYouLikeQuery.getSize());
                platformCategoryParam.setCurrent(guessYouLikeQuery.getCurrent());
                platformCategoryParam.setPlatformCategoryId(platformCategoryList.get(0));
                // 根据 平台三级类目id获取商品信息
                Page<ApiPlatformProductVO> data = goodsRpcService.getProductInfoByPlatformCategoryId(platformCategoryList, platformCategoryParam);
                if (!data.getRecords().isEmpty()) {
                    //获取评论人数
                    Map<Long, Long> evaluatePerson = orderRpcService.getEvaluatePerson(data.getRecords().stream().map(ApiPlatformProductVO::getId).collect(Collectors.toSet()));
                    guessYouLikeVoList = CollectionUtil.emptyIfNull(data.getRecords()).stream().map(apiPlatformProductVO -> {
                        GuessYouLikeVO guessYouLikeVO = new GuessYouLikeVO();
                        if (apiPlatformProductVO != null) {
                            guessYouLikeVO.setProductId(apiPlatformProductVO.getId())
                                    .setShopId(apiPlatformProductVO.getShopId())
                                    .setProductName(apiPlatformProductVO.getName())
                                    .setProductAlbumPics(apiPlatformProductVO.getPic())
                                    .setEvaluated(evaluatePerson.get(apiPlatformProductVO.getId()) == null ? 0L : evaluatePerson.get(apiPlatformProductVO.getId()))
                                    .setLowestPrice(apiPlatformProductVO.getStatistics() != null ? apiPlatformProductVO.getStatistics().getLowestPrice() : 0)
                                    .setSalesVolume(apiPlatformProductVO.getStatistics() != null ? apiPlatformProductVO.getStatistics().getSalesVolume() : 0);
                        }
                        return guessYouLikeVO;
                    }).collect(Collectors.toList());
                }
                if (!guessYouLikeVoList.isEmpty()) {
                    return PageBean.getPage(guessYouLikeQuery, guessYouLikeVoList, data.getTotal(), data.getPages());
                }

            }
        }
        ProductRandomParam productRandomParam = new ProductRandomParam();
        productRandomParam.setCurrent(guessYouLikeQuery.getCurrent())
                .setSize(guessYouLikeQuery.getSize());
        // 获取随机商品
        Page<Product> productPage = goodsRpcService.randomGoods(productRandomParam);
        log.warn("商品信息 :{}", productPage);
        //获取评论人数
        Map<Long, Long> evaluatePerson = orderRpcService.getEvaluatePerson(productPage.getRecords().stream().map(Product::getId).collect(Collectors.toSet()));
        List<ShopProductKeyDTO> shopProductKeys = CollectionUtil.emptyIfNull(productPage.getRecords()).stream().map(
                product -> new ShopProductKeyDTO()
                        .setShopId(product.getSellType() != SellType.CONSIGNMENT ? product.getShopId() : product.getSupplierId())
                        .setProductId(product.getId())
        ).collect(Collectors.toList());
        // 获取销量
        Map<String, ProductStatisticsVO> productStatisticsMap = storageRpcService.getProductStatisticsMap(shopProductKeys);
        guessYouLikeVoList = productPage.getRecords().stream().map(product -> {
            GuessYouLikeVO guessYouLikeVO = new GuessYouLikeVO();
            ProductStatisticsVO productStatisticsVO = productStatisticsMap.get(RedisUtil.key(product.getSellType() != SellType.CONSIGNMENT ? product.getShopId() : product.getSupplierId(), product.getId()));
            guessYouLikeVO.setProductId(product.getId())
                    .setShopId(product.getShopId())
                    .setProductName(product.getName())
                    .setProductAlbumPics(product.getPic())
                    .setEvaluated(evaluatePerson.get(product.getId()) == null ? 0L : evaluatePerson.get(product.getId()));
            if (productStatisticsVO != null) {
                guessYouLikeVO
                        .setLowestPrice(productStatisticsVO.getLowestPrice() != null ? productStatisticsVO.getLowestPrice() : 0)
                        .setSalesVolume(productStatisticsVO.getSalesVolume() != null ? productStatisticsVO.getSalesVolume() : 0);
                //代销商品重新计算价格
                if (product.getSellType() == SellType.CONSIGNMENT) {
                    ConsignmentPriceSettingDTO consignmentPriceSetting = product.getExtra().getConsignmentPriceSetting();
                    boolean isRegular = consignmentPriceSetting.getType() == PricingType.REGULAR;
                    long newPrice = isRegular
                            ? consignmentPriceSetting.getScribe()
                            : (productStatisticsVO.getLowestPrice() * consignmentPriceSetting.getScribe() / 1000000);
                    guessYouLikeVO.setLowestPrice(productStatisticsVO.getLowestPrice() + newPrice);
                }
            }

            return guessYouLikeVO;
        }).collect(Collectors.toList());

        return PageBean.getPage(guessYouLikeQuery, guessYouLikeVoList, productPage.getTotal(), productPage.getPages());

    }


}
