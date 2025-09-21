package com.medusa.gruul.goods.service.mp.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.cart.api.rpc.CartRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.mp.page.PageBean;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.entity.ShopFollow;
import com.medusa.gruul.goods.api.enums.GoodsRabbit;
import com.medusa.gruul.goods.api.model.dto.ShopFollowDTO;
import com.medusa.gruul.goods.api.model.enums.GoodsError;
import com.medusa.gruul.goods.api.model.enums.MyShopFollowStatus;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.param.ApiShopFollowParam;
import com.medusa.gruul.goods.api.model.vo.ApiFollowShopProductVO;
import com.medusa.gruul.goods.api.model.vo.ApiProductPopularAttentionVO;
import com.medusa.gruul.goods.service.model.vo.MyShopFollowVO;
import com.medusa.gruul.goods.service.model.vo.ShopFollowVO;
import com.medusa.gruul.goods.service.mp.mapper.ShopFollowMapper;
import com.medusa.gruul.goods.service.mp.mapper.ShopFollowNewProductsMapper;
import com.medusa.gruul.goods.service.mp.service.IProductService;
import com.medusa.gruul.goods.service.mp.service.IShopFollowService;
import com.medusa.gruul.goods.service.util.GoodsUtil;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.shop.api.model.dto.ShopOperationDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 店铺关注 服务实现类
 *
 * @author WuDi
 * @since 2022-08-03
 */
@Service
@RequiredArgsConstructor
public class ShopFollowServiceImpl extends ServiceImpl<ShopFollowMapper, ShopFollow> implements IShopFollowService {

    private final IProductService productService;
    private final ShopFollowNewProductsMapper shopFollowNewProductsMapper;
    private final OrderRpcService orderRpcService;

    private final RabbitTemplate rabbitTemplate;
    private ShopRpcService shopRpcService;
    private UserRpcService userRpcService;
    private CartRpcService cartRpcService;

    /**
     * 关注/取消关注店铺
     *
     * @param shopFollow 店铺关注DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void follow(ShopFollowDTO shopFollow) {
        Long userId = ISecurity.userMust().getId();
        if (shopFollow.getIsFollow()) {
            //关注
            boolean isFollow = this.lambdaQuery()
                    .eq(ShopFollow::getUserId, userId)
                    .eq(ShopFollow::getShopId, shopFollow.getShopId())
                    .exists();
            GoodsError.YOU_FOLLOWED_THIS_SHOP.trueThrow(isFollow);
            // 用户id 店铺id 关注
            boolean success = this.save(new ShopFollow()
                    .setUserId(userId)
                    .setShopId(shopFollow.getShopId())
                    .setShopName(shopFollow.getName())
                    .setShopLogo(shopFollow.getShopLogo())
            );
            SystemCode.DATA_ADD_FAILED.falseThrow(success);
            //发送店铺关注mq
            rabbitTemplate.convertAndSend(
                    GoodsRabbit.SHOP_ATTENTION.exchange(),
                    GoodsRabbit.SHOP_ATTENTION.routingKey(),
                    new ShopOperationDTO().setUserId(userId)
                            .setShopIds(Collections.singleton(shopFollow.getShopId()))
            );
        } else {
            // 取消关注
            boolean remove = this.lambdaUpdate()
                    .eq(ShopFollow::getUserId, userId)
                    .eq(ShopFollow::getShopId, shopFollow.getShopId())
                    .remove();
            SystemCode.DATA_ADD_FAILED.falseThrow(remove);
        }

        //清除用户关注店铺的缓存
        RedisUtil.delete(GoodsUtil.userFollowShopKey(userId));


    }

    /**
     * 判断用户是否关注当前店铺
     *
     * @param shopId 店铺id
     * @return true 关注 false 未关注
     */
    @Override
    public Boolean isFollow(Long shopId) {
        AtomicBoolean followBoolean = new AtomicBoolean(false);
        ISecurity.match()
                .ifUser((secureUser) -> followBoolean.set(this.lambdaQuery()
                        .eq(ShopFollow::getUserId, secureUser.getId())
                        .eq(ShopFollow::getShopId, shopId)
                        .exists()));
        return followBoolean.get();
    }

    /**
     * 获取关注的店铺
     *
     * @param shopFollowParam 店铺查询参数
     * @return 关注的店铺
     */
    @Override
    public IPage<ShopFollowVO> shopInfo(ApiShopFollowParam shopFollowParam) {
        Long userId = ISecurity.userMust().getId();
        shopFollowParam.setUserId(userId);
        IPage<ShopFollowVO> shopFollowVoPage = TenantShop.disable(() -> baseMapper.pageShopFollow(shopFollowParam));
        //店铺浏览量，根据足迹查询出来
        Set<Long> shopIds = shopFollowVoPage.getRecords().stream().map(ShopFollowVO::getShopId).collect(Collectors.toSet());
        Map<Long, Long> footMarkMap = CollUtil.isEmpty(shopIds) ? new HashMap<>() : userRpcService.getFootMarkCount(shopIds);
        //店铺 加购数
        Map<Long, Integer> shopCartCount = cartRpcService.getShopCartCache(shopIds);
        CollectionUtil.emptyIfNull(shopFollowVoPage.getRecords()).forEach(shopFollow -> {
            ShopInfoVO shopInfoVO = shopRpcService.getShopInfoByShopId(shopFollow.getShopId());
            //店铺类型
            shopFollow.setShopType(shopInfoVO.getShopType());
            //浏览量
            shopFollow.setPv(footMarkMap.get(shopInfoVO.getId()));
            TenantShop.disable(() -> {
                List<Product> productList = productService.lambdaQuery()
                        .select(Product::getId, Product::getPic, Product::getCreateTime)
                        .eq(Product::getShopId, shopFollow.getShopId())
                        .eq(Product::getStatus, ProductStatus.SELL_ON)
                        .apply("DATE_SUB(CURDATE(), INTERVAL 7 DAY) <= date(create_time)")
                        .orderByDesc(Product::getCreateTime)
                        .page(new Page<>(CommonPool.NUMBER_ONE, CommonPool.NUMBER_FOUR)).getRecords();
                shopFollow.setProductList(productList);
                shopFollow.setName(shopInfoVO.getName())
                        .setLogo(shopInfoVO.getLogo())
                        .setNewTips(shopInfoVO.getNewTips());
                //上新时间
                shopFollow.setGoodsCreateTime(Option.of(productList.stream().findFirst().get().getCreateTime()).getOrNull());
            });
            //加购数 shopCartCount
            shopFollow.setBuyMore(shopCartCount.get(shopInfoVO.getId()));
        });

        return shopFollowVoPage;
    }

    @Override
    public IPage<MyShopFollowVO> myFollow(ApiShopFollowParam shopFollowParam, MyShopFollowStatus myShopFollowStatus) {
        Long userId = ISecurity.userMust().getId();
        return switch (myShopFollowStatus) {
            // 全部店铺
            case ALL_SHOP ->
                // 获取关注的全部店铺
                    getShopFollowPage(shopFollowParam, userId);
            // 最近看过
            case RECENTLY ->
                // 查询最近看过的店铺商品
                    shopFollowNewProductsMapper.recentlyPage(shopFollowParam, userId);
            // 有上新
            case NEW_PRODUCTS -> baseMapper.getHasNewProducts(shopFollowParam, userId);
        };

    }

    private IPage<MyShopFollowVO> getShopFollowPage(ApiShopFollowParam shopFollowParam, Long userId) {
        IPage<MyShopFollowVO> shopFollowPage = baseMapper.getShopFollowPage(shopFollowParam, userId);
        List<MyShopFollowVO> records = shopFollowPage.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return shopFollowPage;
        }
        List<Long> shopIds = records.stream().map(MyShopFollowVO::getShopId).collect(Collectors.toList());
        //查询有上新的店铺 只要在一个月之内有上架新商品的店铺就算上新店铺
        List<Long> newUpShopIds = productService.getNewUpShop(shopIds);
        records.forEach(shopFollowVO -> shopFollowVO.setNewProducts(newUpShopIds.contains(shopFollowVO.getShopId())));
        return shopFollowPage;
    }

    /**
     * 根据店铺查询四个上架商品
     *
     * @param shopId 店铺id
     * @return 店铺商品
     */
    private List<ApiProductPopularAttentionVO> getFollowShopProduct(Long shopId) {
        List<Product> list = ISystem.shopId(shopId, () -> productService.lambdaQuery()
                .eq(Product::getStatus, ProductStatus.SELL_ON)
                .orderByDesc(Product::getUpdateTime)
                .last(SqlHelper.limit(CommonPool.NUMBER_ONE, CommonPool.NUMBER_FOUR))
                .list());
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        Map<Long, Long> evaluatePerson = orderRpcService.getEvaluatePerson(list.stream().map(Product::getId).collect(Collectors.toSet()));
        List<ApiProductPopularAttentionVO> vos = new ArrayList<>();
        list.forEach(product -> {
            ApiProductPopularAttentionVO vo = new ApiProductPopularAttentionVO();
            vo.setProductId(product.getId())
                    .setProductName(product.getName())
                    .setProductPrice(product.getSalePrices().get(CommonPool.NUMBER_ZERO))
                    .setPic(product.getPic())
                    .setEvaluated(Option.of(evaluatePerson.get(product.getId())).getOrElse(Long.valueOf(CommonPool.NUMBER_ZERO)));
            vos.add(vo);
        });
        return vos;
    }

    /**
     * 首页我的关注全部店铺
     *
     * @param shopFollowParam 店铺查询参数
     * @return 我的关注全部店铺
     */
    @Override
    public IPage<MyShopFollowVO> homePageMyFollow(ApiShopFollowParam shopFollowParam) {
        shopFollowParam.setUserId(ISecurity.userMust().getId());
        return baseMapper.homePageMyFollow(shopFollowParam);
    }


    /**
     * pc端-关注店铺
     *
     * @param shopFollowParam 店铺参数
     * @return 我的关注
     */
    @Override
    public IPage<ApiFollowShopProductVO> shopFollowProducts(ApiShopFollowParam shopFollowParam) {
        ApiShopFollowParam page = this.lambdaQuery()
                .select(ShopFollow::getShopId)
                .eq(ShopFollow::getUserId, ISecurity.userMust().getId())
                .page(shopFollowParam);
        List<ShopFollow> shopFollowList = page.getRecords();
        if (CollUtil.isEmpty(shopFollowList)) {
            return null;
        }
        List<ApiFollowShopProductVO> followShopProductVOList = shopFollowList.stream().map(shopFollow -> {
            ApiFollowShopProductVO followShopProductVO = new ApiFollowShopProductVO();
            followShopProductVO.setShopId(shopFollow.getShopId())
                    .setShopName(shopFollow.getShopName())
                    .setShopLogo(shopFollow.getShopLogo());
            // 查询销量最高的商品
            followShopProductVO.setProductSaleVolumeVOList(productService.shopHotSales(shopFollow.getShopId(), null));
            return followShopProductVO;
        }).collect(Collectors.toList());
        return PageBean.getPage(page, followShopProductVOList, page.getTotal(), page.getPages());
    }

    @Override
    public void updateShopFollowInfo(ShopInfoVO shopInfoVO) {
        boolean exists = this.lambdaQuery()
                .eq(ShopFollow::getShopId, shopInfoVO.getId())
                .exists();
        if (!exists) {
            return;
        }
        boolean update = this.lambdaUpdate()
                .set(ShopFollow::getShopName, shopInfoVO.getName())
                .set(ShopFollow::getShopLogo, shopInfoVO.getLogo())
                .eq(ShopFollow::getShopId, shopInfoVO.getId())
                .update();
        if (!update) {
            throw new GlobalException();
        }
    }

    /**
     * 获取店铺关注人数
     *
     * @param shopId 店铺id
     * @return 关注人数
     */
    @Override
    public Long followCount(Long shopId) {
        return this.lambdaQuery()
                .eq(ShopFollow::getShopId, shopId)
                .count();
    }

    @Lazy
    @Autowired
    public void setShopRpcService(ShopRpcService shopRpcService) {
        this.shopRpcService = shopRpcService;
    }

    @Lazy
    @Autowired
    public void setUserRpcService(UserRpcService userRpcService) {
        this.userRpcService = userRpcService;
    }

    @Lazy
    @Autowired
    public void setCartRpcService(CartRpcService cartRpcService) {
        this.cartRpcService = cartRpcService;
    }
}
