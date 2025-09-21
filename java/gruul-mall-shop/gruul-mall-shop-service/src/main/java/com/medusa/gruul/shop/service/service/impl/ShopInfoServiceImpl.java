package com.medusa.gruul.shop.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.global.model.o.RangeTime;
import com.medusa.gruul.goods.api.model.vo.ShopFollowVO;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.order.api.model.OrderEvaluateCountDTO;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.search.api.model.ProductVO;
import com.medusa.gruul.search.api.model.SearchCouponVO;
import com.medusa.gruul.search.api.rpc.SearchRpcService;
import com.medusa.gruul.shop.api.constant.ShopConstant;
import com.medusa.gruul.shop.api.entity.Shop;
import com.medusa.gruul.shop.api.enums.ShopRabbit;
import com.medusa.gruul.shop.api.model.dto.ShopIcDistributeInfoDTO;
import com.medusa.gruul.shop.api.model.dto.ShopInfoDTO;
import com.medusa.gruul.shop.api.model.dto.ShopOperationDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.service.model.ShopConstants;
import com.medusa.gruul.shop.service.model.dto.ShopDetailDTO;
import com.medusa.gruul.shop.service.model.dto.ShopDetailVO;
import com.medusa.gruul.shop.service.model.dto.ShopSearchParamDTO;
import com.medusa.gruul.shop.service.model.enums.DetailQueryType;
import com.medusa.gruul.shop.service.model.enums.ShopError;
import com.medusa.gruul.shop.service.model.vo.GdMapVO;
import com.medusa.gruul.shop.service.model.vo.ShopAuditVO;
import com.medusa.gruul.shop.service.model.vo.ShopSearchVO;
import com.medusa.gruul.shop.service.mp.service.IShopService;
import com.medusa.gruul.shop.service.properties.GdConfig;
import com.medusa.gruul.shop.service.properties.ShopConfigurationProperties;
import com.medusa.gruul.shop.service.service.ShopInfoService;
import com.medusa.gruul.shop.service.service.addon.CouponAddonSupporter;
import com.medusa.gruul.shop.service.service.addon.ShopAddonSupporter;
import com.medusa.gruul.shop.service.util.ShopUtil;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.Lists;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author xiaoq date 2022-05-26 11:28
 */
@Service
@RequiredArgsConstructor
public class ShopInfoServiceImpl implements ShopInfoService {

    private final IShopService shopService;
    private final Executor globalExecutor;
    private final RabbitTemplate rabbitTemplate;
    private final StorageRpcService storageRpcService;
    private final ShopAddonSupporter shopAddonSupporter;
    private final CouponAddonSupporter couponAddonSupporter;
    private final ShopConfigurationProperties shopConfigurationProperties;
    private final SearchRpcService searchRpcService;
    private final GdConfig gdConfig;
    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private OrderRpcService orderRpcService;
    private GoodsRpcService goodsRpcService;


    /**
     * 店铺设置信息修改 并入redis
     *
     * @param shopInfo 店铺修改信息
     */
    @Override
    public void updateShopInfo(ShopInfoDTO shopInfo) {
        Long shopId = ISecurity.userMust().getShopId();
        Shop shop = shopService.getById(shopId);
        if (shop == null) {
            throw ShopError.SHOP_NOT_EXIST.exception();
        }
        boolean success = RedisUtil.doubleDeletion(
                () -> shopService.lambdaUpdate()
                        .set(Shop::getLogo, shopInfo.getLogo())
                        .set(Shop::getHeadBackground, shopInfo.getHeadBackground())
                        .set(Shop::getCompanyName, shopInfo.getCompanyName())
                        .set(Shop::getStart, shopInfo.getStart())
                        .set(Shop::getEnd, shopInfo.getEnd())
                        .set(Shop::getName, shopInfo.getName())
                        .set(Shop::getContractNumber, shopInfo.getContractNumber())
                        .set(Shop::getNewTips, shopInfo.getNewTips())
                        .set(Shop::getBriefing, shopInfo.getBriefing())
                        .eq(BaseEntity::getId, shopId)
                        .update(),
                ShopConstants.SHOP_BASE_INFO,
                shopId
        );
        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
        shop.setName(shopInfo.getName());
        globalExecutor.execute(
                () -> {
                    rabbitTemplate.convertAndSend(
                            ShopRabbit.SHOP_UPDATE.exchange(),
                            ShopRabbit.SHOP_UPDATE.routingKey(),
                            ShopInfoVO.fromShop(shop)
                    );
                    pigeonChatStatisticsRpcService.removeIMShopInfoCache(shopId);
                }
        );


    }

    @Override
    public Option<Shop> getShopById(Long shopId) {
        ShopConfigurationProperties.CacheExpire cacheExpire = shopConfigurationProperties.getCacheExpire();
        return Option.of(
                RedisUtil.getCacheMap(
                        Shop.class,
                        () -> shopService.getById(shopId),
                        Duration.ofSeconds(cacheExpire.getShopExpireTime()),
                        ShopConstants.SHOP_BASE_INFO,
                        shopId
                )
        );
    }

    /**
     * 获取符合条件的店铺 id 集合
     *
     * @param productIsNotEmpty 是否过滤商品为空的店铺
     * @param paramShopIds      查询条件店铺 id 集合
     * @return Set<Long> 符合条件的店铺 id 集合 返回 null 代表没有任何符合条件的店铺 返回 empty set 代表不需要过滤店铺 返回非 empty set 代表需要过滤店铺
     */
    private List<Long> getShopIds(boolean productIsNotEmpty, List<Long> paramShopIds) {
        //如果需要查询有商品的店铺
        if (!productIsNotEmpty) {
            return CollUtil.emptyIfNull(paramShopIds);
        }
        //查询有商品可售的店铺
        Set<Long> sellProductShopIds = searchRpcService.getSellProductShopIds();
        //如果没有符合条件的店铺
        if (CollUtil.isEmpty(sellProductShopIds)) {
            return null;
        }
        //如果不需要查询指定店铺信息 直接返回有商品可售的店铺id 即可
        if (CollUtil.isEmpty(paramShopIds)) {
            return List.copyOf(sellProductShopIds);
        }
        //如果需要查询指定店铺信息
        //取交集
        paramShopIds = paramShopIds.stream().filter(sellProductShopIds::contains).toList();
        //如果没有符合条件的店铺
        if (CollUtil.isEmpty(paramShopIds)) {
            return null;
        }
        return paramShopIds;
    }

    /**
     * C端店铺搜索
     *
     * @param param 搜索条件
     * @return 查询店铺结果
     */
    @Override
    public IPage<ShopSearchVO> searchShop(ShopSearchParamDTO param) {
        List<Long> shopIds = getShopIds(param.getProductIsNotEmpty(), param.getShopIds());
        if (shopIds == null) {
            return new Page<>();
        }
        //店铺 id 是否不为空
        boolean notEmptyShopIds = CollUtil.isNotEmpty(shopIds);
        //是否需要按照店铺 id 列表排序
        boolean orderByShopIds = CollUtil.isNotEmpty(param.getShopIds()) && notEmptyShopIds;
        //如果需要查询指定店铺信息
        IPage<ShopSearchVO> shopPage = shopService.lambdaQuery()
                .select(
                        Shop::getId,
                        Shop::getName,
                        Shop::getLogo,
                        Shop::getNewTips,
                        Shop::getHeadBackground,
                        Shop::getAddress,
                        Shop::getShopType
                )
                .like(StrUtil.isNotEmpty(param.getName()), Shop::getName, param.getName())
                .eq(Shop::getStatus, ShopStatus.NORMAL)
                .ne(Shop::getShopMode, ShopMode.SUPPLIER)
                .in(notEmptyShopIds, Shop::getId, shopIds)
                .eq(param.getShopType() != null, Shop::getShopType, param.getShopType())
                .orderByDesc(!orderByShopIds, BaseEntity::getCreateTime)
                .last(orderByShopIds, "ORDER BY FIELD(id, " + shopIds.stream().map(String::valueOf)
                        .collect(Collectors.joining(StrPool.COMMA)) + ");")
                .page(param)
                .convert(ShopSearchVO::fromShop);
        //如果没有查到店铺信息 或者 不是消费者端查询 或者 用户未登录 直接返回
        if (shopPage.getTotal() <= CommonPool.NUMBER_ZERO) {
            return shopPage;
        }
        List<ShopSearchVO> records = shopPage.getRecords();
        Set<Long> selectedShopIds = records.stream().map(ShopSearchVO::getId).collect(Collectors.toSet());
        if (param.getSalesRanking() > 0) {
            Map<Long, List<ProductVO>> shopIdProductsMap = searchRpcService.salesRanking(selectedShopIds,
                    param.getSalesRanking());
            records.forEach(
                    record -> record.setProducts(shopIdProductsMap.getOrDefault(record.getId(), List.of()))
            );
        }
        if (param.getScored()) {
            Map<Long, OrderEvaluateCountDTO> orderEvaluateCount = orderRpcService.getOrderEvaluateCount(
                    selectedShopIds);
            records.forEach(
                    record -> {
                        OrderEvaluateCountDTO evaluated = orderEvaluateCount.get(record.getId());
                        record.setScore(evaluated == null ? OrderEvaluateCountDTO.defaultScore() : evaluated.score());
                    }
            );
        }
        Long userId;
        if (!param.getUserSearch() || ClientType.CONSUMER != ISystem.clientTypeMust()
                || (userId = ISecurity.userOpt().map(SecureUser::getId).getOrNull()) == null) {
            return shopPage;
        }
        rabbitTemplate.convertAndSend(
                ShopRabbit.SHOP_SEARCH.exchange(),
                ShopRabbit.SHOP_SEARCH.routingKey(),
                new ShopOperationDTO()
                        .setShopIds(selectedShopIds)
                        .setUserId(userId)
        );

        return shopPage;
    }

    /**
     * 根据销量查询店铺
     *
     * @param sortAsc 是否升序
     * @return 查询店铺结果
     */
    @Override
    public List<ShopInfoVO> searchShopBySales(Boolean sortAsc) {
        // 查询店铺销量
        List<ProductStatisticsVO> shopSales = storageRpcService.getShopSales(sortAsc);
        if (CollectionUtils.isEmpty(shopSales)) {
            return null;
        }
        List<Long> shopIds = shopSales.stream()
                .map(ProductStatisticsVO::getShopId)
                .toList();
        List<Shop> shops = shopService.lambdaQuery()
                .in(Shop::getId, shopIds)
                .eq(Shop::getShopMode, ShopMode.COMMON)
                .eq(Shop::getStatus, ShopStatus.NORMAL)
                .last("ORDER BY FIELD(id," + shopIds.stream().map(String::valueOf)
                        .collect(Collectors.joining(StrPool.COMMA)) + ")")
                .list();
        if (CollectionUtil.isEmpty(shops)) {
            return null;
        }
        List<Long> list = shops.stream().map(Shop::getId).toList();
        //查询存在上架商品的店铺ids
        list = searchRpcService.queryHasSellOnProductShopIds(list);
        List<Long> finalList = list;
        shops = shops.stream().filter(shop -> finalList.contains(shop.getId())).toList();
        //店铺优惠券
        Set<Long> shopIdList = shops.stream().map(Shop::getId).collect(Collectors.toSet());
        List<SearchCouponVO> couponList = couponAddonSupporter.getShopCouponList(shopIdList);
        Map<Long, List<SearchCouponVO>> couponMap = CollUtil.isNotEmpty(couponList) ? couponList.stream()
                .collect(Collectors.groupingBy(SearchCouponVO::getShopId)) : new HashMap<>();

        Map<Long, Long> shopSalesMap = shopSales.stream()
                .collect(Collectors.toMap(ProductStatisticsVO::getShopId, ProductStatisticsVO::getSalesVolume));
        Map<Long, OrderEvaluateCountDTO> orderEvaluateMap = orderRpcService.getOrderEvaluateCount(shopIdList);
        return shops.stream()
                .map(shop -> ShopInfoVO.fromShop(shop)
                        .setScore(orderEvaluateMap.getOrDefault(shop.getId(), new OrderEvaluateCountDTO()).score())
                        .setSalesVolume(shopSalesMap.get(shop.getId()))
                        .setCouponList(couponMap.get(shop.getId())))
                .collect(Collectors.toList());

    }


    /**
     * 根据距离查询店铺
     *
     * @param longitude         经度
     * @param latitude          纬度
     * @param sortAsc           是否升序
     * @param userId
     * @param showHeaderShopIds
     * @param moreCount
     * @return 查询店铺结果
     */
    @Override
    public List<ShopInfoVO> searchShopByDistance(Double longitude, Double latitude,
                                                 Boolean sortAsc, Long userId, List<Long> showHeaderShopIds,
                                                 Integer moreCount) {

        RedisTemplate<String, Object> redisTemplate = RedisUtil.getRedisTemplate();
        // 店铺数量
        Long size = redisTemplate.opsForZSet().size(ShopConstant.SHOP_GEO);
        if (size == null || size == 0) {
            return List.of();
        }
        Set<Long> shopIds;

        GeoResults<RedisGeoCommands.GeoLocation<Object>> radius = null;
        if (Objects.nonNull(longitude) && Objects.nonNull(latitude)) {
            //存在经纬度参数 则判断距离
            RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                    .includeDistance()
                    .includeCoordinates()
                    .sort(sortAsc ? Sort.Direction.ASC : Sort.Direction.DESC)
                    .limit(size);
            Circle circle = new Circle(longitude, latitude, Double.MAX_VALUE);
            radius = redisTemplate.opsForGeo()
                    .radius(ShopConstant.SHOP_GEO, circle, args);
            if (radius == null) {
                return List.of();
            }
            shopIds = radius.getContent()
                    .stream()
                    .map(geoLocationGeoResult ->
                            Long.valueOf(geoLocationGeoResult.getContent().getName().toString())
                    ).collect(Collectors.toSet());
        } else {
            shopIds = redisTemplate.opsForZSet().range(ShopConstant.SHOP_GEO, 0, -1).stream()
                    .map(x -> Long.valueOf(x.toString())).collect(Collectors.toSet());
        }
        if (CollectionUtil.isEmpty(shopIds)) {
            return List.of();
        }

        // 该处应该是正常运营店铺
        Set<Long> finalShopIds = shopService.lambdaQuery()
                .eq(Shop::getStatus, ShopStatus.NORMAL)
                .eq(Shop::getShopMode, ShopMode.O2O)
                .in(BaseEntity::getId, shopIds).list()
                .stream().map(BaseEntity::getId).collect(Collectors.toSet());
        if (CollectionUtil.isEmpty(finalShopIds)) {
            return List.of();
        }

        //查询存在上架商品的店铺ids
        List<Long> hasSellOnProductShopIds = searchRpcService.queryHasSellOnProductShopIds(
                new ArrayList<>(finalShopIds));
        finalShopIds = new HashSet<>(hasSellOnProductShopIds);
        if (CollectionUtil.isEmpty(finalShopIds)) {
            return List.of();
        }
        Map<Long, BigDecimal> shopInitialDeliveryCharge = shopAddonSupporter.shopInitialDeliveryCharge(shopIds);
        if (CollUtil.isEmpty(shopInitialDeliveryCharge)) {
            shopInitialDeliveryCharge = CollectionUtils.newHashMap(1);
        }
        Map<Long, Long> shopSalesMap = CollectionUtil.emptyIfNull(storageRpcService.getShopSales(sortAsc))
                .stream()
                .collect(Collectors.toMap(ProductStatisticsVO::getShopId, ProductStatisticsVO::getSalesVolume));
        Map<Long, BigDecimal> finalShopInitialDeliveryCharge = shopInitialDeliveryCharge;
        List<ShopInfoVO> list;
        if (Objects.nonNull(radius)) {
            Set<Long> finalShopIds1 = finalShopIds;
            list = radius.getContent()
                    .stream()
                    .filter(geoLocationGeoResult ->
                            finalShopIds1.contains(Long.valueOf(geoLocationGeoResult.getContent().getName().toString()))
                                    &&
                                    !getShopById(
                                            Long.valueOf(
                                                    geoLocationGeoResult.getContent().getName().toString())).isEmpty())
                    .map(geoLocationGeoResult -> {
                        double distance = ShopUtil.getDistance(longitude, latitude,
                                geoLocationGeoResult.getContent().getPoint().getX(),
                                geoLocationGeoResult.getContent().getPoint().getY());
                        Long shopId = Long.valueOf(geoLocationGeoResult.getContent().getName().toString());
                        Shop shop = getShopById(shopId).get();
                        return ShopInfoVO.fromShop(shop)
                                .setDistance(distance)
                                .setSalesVolume(shopSalesMap.get(shopId))
                                .setInitialDeliveryCharge(finalShopInitialDeliveryCharge.get(shopId));
                    }).toList();
        } else {
            list = finalShopIds.stream()
                    .map(shopId -> ShopInfoVO.fromShop(getShopById(shopId).get())
                            .setSalesVolume(shopSalesMap.get(shopId))
                            .setInitialDeliveryCharge(finalShopInitialDeliveryCharge.get(shopId))
                    )
                    .toList();
        }
        if (Objects.nonNull(userId)) {
            Set<Long> followedShopIds = goodsRpcService.queryShopIsUserFollow(userId, finalShopIds);
            list = list.stream().map(shopInfoVO -> {
                if (followedShopIds.contains(shopInfoVO.getId())) {
                    shopInfoVO.setIsFollow(Boolean.TRUE);
                } else {
                    shopInfoVO.setIsFollow(Boolean.FALSE);
                }
                return shopInfoVO;
            }).toList();
        }

        List<ShopInfoVO> result = com.google.common.collect.Lists.newArrayListWithCapacity(list.size());
        if (CollectionUtil.isNotEmpty(showHeaderShopIds)) {
            Map<Long, ShopInfoVO> shopInfoVOMap = list.stream().collect(Collectors.toMap(ShopInfoVO::getId, Function.identity()));
            // 添加头部的店铺
            for (Long showHeaderShopId : showHeaderShopIds) {
                ShopInfoVO shopInfoVO = shopInfoVOMap.get(showHeaderShopId);
                if (Objects.nonNull(shopInfoVO)) {
                    result.add(shopInfoVO);
                }
            }
            // 从原列表中移除头部的店铺
            list = list.stream()
                    .filter(shopInfoVO -> !showHeaderShopIds.contains(shopInfoVO.getId()))
                    .toList();
        }
        if (Objects.nonNull(moreCount)) {
            list = list.stream().limit(moreCount).collect(Collectors.toList());
        }
        result.addAll(list);
        shopIds = result.stream().map(ShopInfoVO::getId).collect(Collectors.toSet());
        List<SearchCouponVO> couponList = couponAddonSupporter.getShopCouponList(shopIds);
        Map<Long, List<SearchCouponVO>> shopCouponMap = couponList.stream().collect(Collectors.groupingBy(SearchCouponVO::getShopId));
        result.forEach(shopInfoVO -> {
            List<SearchCouponVO> couponVOList = shopCouponMap.get(shopInfoVO.getId());
            if (CollectionUtil.isNotEmpty(couponVOList)) {
                // 优惠券只显示一个
                shopInfoVO.setCouponList(couponVOList.stream().limit(1).toList());
            }
        });
        calcDistributeInfo(longitude, latitude, result);
        return result;
    }

    /**
     * 计算同城配送
     *
     * @param longitude
     * @param latitude
     * @param list
     */
    private void calcDistributeInfo(Double longitude, Double latitude, List<ShopInfoVO> list) {
        if (Objects.isNull(longitude) || Objects.isNull(latitude)) {
            return;
        }
        GeometryFactory factory = new GeometryFactory();
        Coordinate coord = new Coordinate(longitude, latitude); // 北京坐标
        Point point = factory.createPoint(coord);
        Set<Long> shopIds = list.stream().map(ShopInfoVO::getId).collect(Collectors.toSet());
        Map<Long, ShopIcDistributeInfoDTO> shopIcDistributeInfo = shopAddonSupporter.getShopIcDistributeInfo(shopIds, point);
        for (ShopInfoVO shopInfoVO : list) {
            shopInfoVO.setIcDistributeInfo(shopIcDistributeInfo.get(shopInfoVO.getId()));
        }

    }


    @Override
    public ShopInfoVO getShopSaleAndDistance(Long shopId, Double longitude, Double latitude) {
        Shop shop = getShopById(shopId).get();
        if (shop == null) {
            return null;
        }
        double distance = 0;
        if (longitude != null && latitude != null) {
            Point location = shop.getLocation();
            distance = ShopUtil.getDistance(longitude, latitude, location.getX(), location.getY());
        }
        Long shopSaleVolume = storageRpcService.getShopSaleVolume(shopId);
        return new ShopInfoVO()
                .setId(shopId)
                .setDistance(distance)
                .setSalesVolume(shopSaleVolume);
    }

    /**
     * 获取供应商信息
     *
     * @param supplierName 供应商名称
     * @return List<获取供应商信息>
     */
    @Override
    public List<Shop> getSupplierInfo(String supplierName) {
        return shopService.lambdaQuery().select(
                BaseEntity::getId,
                Shop::getName
        ).eq(Shop::getShopMode, ShopMode.SUPPLIER).like(Shop::getName, supplierName).list();
    }

    /**
     * 客户端 关注页面 查询推荐店铺
     *
     * @return IPage ShopInfoVO
     */
    @Override
    public IPage<ShopInfoVO> searchRecommendationShop(ShopSearchParamDTO param) {
        Long userId = ISecurity.userOpt().map(SecureUser::getId).getOrNull();
        //登录情况下，有关注店铺直接return null
        if (userId != null && goodsRpcService.shopFollow(userId) > CommonPool.NUMBER_ZERO) {
            return new Page<>();
        }
        Set<Long> shopIds = searchRpcService.getSellProductShopIds();
        if (CollUtil.isEmpty(shopIds)) {
            return new Page<>();
        }
        IPage<ShopInfoVO> shopInfoPage = shopService.lambdaQuery()
                .select(Shop::getId, Shop::getName, Shop::getLogo, Shop::getShopType, Shop::getShopMode,
                        Shop::getLocation)
                .eq(Shop::getStatus, ShopStatus.NORMAL)
                .ne(Shop::getShopMode, ShopMode.SUPPLIER)
                .in(Shop::getId, shopIds)
                .eq(BaseEntity::getDeleted, Boolean.FALSE)
                .orderByAsc(Shop::getShopMode)
                .page(param)
                .convert(ShopInfoVO::fromShop);
        //店铺ids
        shopIds = shopInfoPage.getRecords().stream().map(ShopInfoVO::getId).collect(Collectors.toSet());
        //优惠券
        List<SearchCouponVO> couponList = couponAddonSupporter.getShopCouponList(shopIds);
        Map<Long, List<SearchCouponVO>> couponMap = CollUtil.isNotEmpty(couponList) ? couponList.stream()
                .collect(Collectors.groupingBy(SearchCouponVO::getShopId)) : new HashMap<>();
        //销量
        List<ProductStatisticsVO> productStatisticsList = storageRpcService.getShopListSales(shopIds);
        Map<Long, Long> productStatisticsMap = productStatisticsList.stream().collect(Collectors.toMap(
                ProductStatisticsVO::getShopId,
                ProductStatisticsVO::getSalesVolume
        ));
        //O2O店铺ids
        Set<Long> o2oShopIds = shopInfoPage.getRecords().stream()
                .filter(shopInfo -> ShopMode.O2O.equals(shopInfo.getShopMode()))
                .map(ShopInfoVO::getId).collect(Collectors.toSet());
        //起送费
        Map<Long, BigDecimal> shopInitialDeliveryCharge = CollUtil.isEmpty(o2oShopIds)
                ? new HashMap<>()
                : Optional.ofNullable(shopAddonSupporter.shopInitialDeliveryCharge(o2oShopIds)).orElse(new HashMap<>());
        //店铺下最新的5条商品
        Map<Long, List<com.medusa.gruul.goods.api.model.vo.ProductVO>> productMap =
                goodsRpcService.getTopFiveProductOrderTime(
                        shopIds);
        //请求ip
        if (param.getLongitude() == null || param.getLatitude() == null) {
            param.setLongitude(null);
            String requestIp = ISystem.ipOpt().getOrNull();
            String ip = NetUtil.isInnerIP(requestIp) ? gdConfig.getIp() : requestIp;
            org.springframework.data.geo.Point point = getPoint(ip);
            if (point != null) {
                param.setLongitude(point.getX());
                param.setLatitude(point.getY());
            }
        }
        //设置 优惠券、商品、销量、起送费、距离
        shopInfoPage.getRecords().forEach(shopInfo -> {
            Long shopId = shopInfo.getId();
            //优惠券
            shopInfo.setCouponList(couponMap.get(shopId));
            //起送费
            shopInfo.setInitialDeliveryCharge(shopInitialDeliveryCharge.get(shopId));
            //销量
            shopInfo.setSalesVolume(productStatisticsMap.get(shopId));
            //商品
            shopInfo.setProductList(productMap.get(shopId));

            //距离
            if (ShopMode.O2O.equals(shopInfo.getShopMode()) && param.getLongitude() != null) {
                shopInfo.setDistance(
                        ShopUtil.getDistance(
                                param.getLongitude(), param.getLatitude(),
                                shopInfo.getLocation().getX(), shopInfo.getLocation().getY()
                        )
                );
            }
        });
        return shopInfoPage;
    }

    private org.springframework.data.geo.Point getPoint(String ip) {
        //请求高德返回经纬度 缓存
        ShopConfigurationProperties.CacheExpire cacheExpire = shopConfigurationProperties.getCacheExpire();
        Option<GdMapVO> map = Option.of(
                RedisUtil.getCacheMap(
                        GdMapVO.class,
                        () -> {
                            String url = StrUtil.format(gdConfig.getUrl(), ip, gdConfig.getKey());
                            String body = HttpUtil.createGet(url).timeout(1000 * 10).execute().body();
                            return FastJson2.convert(body, GdMapVO.class);
                        },
                        Duration.ofSeconds(cacheExpire.getShopExpireTime()),
                        ShopConstants.SHOP_GD_MAP,
                        ip
                )
        );
        Double longitude = map.get().getLongitude();
        Double latitude = map.get().getLatitude();
        if (longitude == null || latitude == null) {
            return null;
        }
        return new org.springframework.data.geo.Point(longitude, latitude);
    }

    /**
     * 跟进店铺ids 获取正常的店铺数据
     *
     * @param shopIds 店铺ids
     * @return List<Shop>
     */
    @Override
    public List<Shop> getBatchShop(Set<Long> shopIds) {
        return shopService.lambdaQuery()
                .eq(Shop::getStatus, ShopStatus.NORMAL)
                .eq(BaseEntity::getDeleted, Boolean.FALSE)
                .in(BaseEntity::getId, shopIds).list();
    }

    /**
     * 根据店铺id查询店铺关注信息
     *
     * @param shopId 店铺id
     * @return 店铺关注信息
     */
    @Override
    public ShopInfoVO getShopInfoFollow(Long shopId) {
        return getShopById(shopId)
                .map(shopInfo -> ShopInfoVO.fromShop(shopInfo)
                        .setFollowCount(
                                goodsRpcService.followCount(shopInfo.getId())
                        )
                        .setScore(
                                Option.of(
                                                orderRpcService.getOrderEvaluateCount(
                                                        Collections.singleton(shopInfo.getId())
                                                ).get(shopInfo.getId())
                                        ).map(OrderEvaluateCountDTO::score)
                                        .getOrElse(OrderEvaluateCountDTO::defaultScore)
                        )).getOrNull();
    }

    @Override
    public ShopDetailVO shopDetail(Long userId, ShopDetailDTO param) {
        Long shopId = param.getShopId();
        Shop shop = getShopById(shopId)
                .getOrElseThrow(ShopError.SHOP_NOT_EXIST::exception);
        ShopMode shopMode = shop.getShopMode();
        Point shopLocation = shop.getLocation();
        ShopDetailVO detail = new ShopDetailVO()
                .setStatus(shop.getStatus())
                .setShopMode(shopMode)
                .setShopType(shop.getShopType())
                .setName(shop.getName())
                .setLogo(shop.getLogo())
                .setLocation(shopLocation);
        //直线距离
        Point location = param.getLocation();
        if (location != null && shopLocation != null) {
            detail.setDistance(
                    BigDecimal.valueOf(
                            ShopUtil.getDistance(
                                    location.getX(), location.getY(),
                                    shopLocation.getX(), shopLocation.getY()
                            )
                    )
            );
        }
        List<Runnable> tasks = new ArrayList<>();
        //关注人数及当前用户是否关注
        tasks.add(
                () -> {
                    ShopFollowVO shopFollow = goodsRpcService.batchShopFollow(Set.of(shopId), userId)
                            .get(shopId);
                    detail.setFollow(shopFollow.getFollow())
                            .setIsFollow(shopFollow.getIsFollow());
                }
        );
        //店铺评分
        tasks.add(
                () -> {
                    OrderEvaluateCountDTO evaluateCount = orderRpcService.getOrderEvaluateCount(Set.of(shopId))
                            .get(shopId);
                    detail.setScore(
                            evaluateCount == null ? OrderEvaluateCountDTO.defaultScore() : evaluateCount.score());
                }
        );

        //店铺首页需要的数据
        DetailQueryType type = param.getType();
        if (DetailQueryType.SHOP_HOME == type) {
            detail.setNo(shop.getNo())
                    .setCompanyName(shop.getCompanyName())
                    .setBriefing(shop.getBriefing())
                    .setHeadBackground(shop.getHeadBackground())
                    .setContractNumber(shop.getContractNumber())
                    .setArea(shop.getArea())
                    .setAddress(shop.getAddress())
                    .setOpenTime(new RangeTime().setStart(shop.getStart()).setEnd(shop.getEnd()))
                    .setNewTips(shop.getNewTips());
            //销量 &
            tasks.add(
                    () -> detail.setSales(ObjectUtil.defaultIfNull(storageRpcService.getShopSaleVolume(shopId), 0L))
            );
            // 商品数量
            tasks.add(
                    () -> detail.setProductNum(goodsRpcService.batchSellProductCount(Set.of(shopId)).get(shopId))
            );

        }

        //如果是O2O店铺 则获取起送费
        if (ShopMode.O2O == shopMode) {
            tasks.add(
                    () -> {
                        //起送费  单位元
                        BigDecimal minLimit = MapUtil.emptyIfNull(
                                        shopAddonSupporter.shopInitialDeliveryCharge(Set.of(shopId)))
                                .get(shopId);
                        detail.setMinLimitPrice(minLimit == null ? 0L : AmountCalculateHelper.toMilli(minLimit));
                    }
            );
        }
        //任务两两分割 组成同一个任务 然后执行
        Runnable[] splitTasks = ListUtil.split(tasks, CommonPool.NUMBER_TWO)
                .stream()
                .map(curTasks -> (Runnable) () -> curTasks.forEach(Runnable::run))
                .toArray(Runnable[]::new);
        //执行异步任务
        CompletableTask.getOrThrowException(
                CompletableTask.allOf(globalExecutor, splitTasks)
        );
        return detail;
    }

    @Override
    public List<Shop> getShopInfo(String shopName, Boolean includeSupplier) {
        List<ShopMode> models = com.google.common.collect.Lists.newArrayList(ShopMode.COMMON, ShopMode.O2O);
        if (includeSupplier) {
            models.add(ShopMode.SUPPLIER);
        }
        return shopService.lambdaQuery().select(
                BaseEntity::getId,
                Shop::getName
        ).in(Shop::getShopMode, models).like(Shop::getName, shopName).list();
    }

    @Override
    public ShopAuditVO getShopAuditInfo(Long userId, ShopMode shopMode) {
        List<ShopMode> shopModes = Lists.newArrayList();
        if (ShopMode.COMMON.equals(shopMode)) {
            shopModes.add(ShopMode.O2O);
            shopModes.add(ShopMode.COMMON);
        } else {
            shopModes.add(ShopMode.SUPPLIER);
        }
        Shop shop = shopService.lambdaQuery().select(
                        Shop::getExtra,
                        com.medusa.gruul.common.mp.model.base.BaseEntity::getCreateTime,
                        Shop::getStatus,
                        Shop::getCompanyName,
                        Shop::getName
                )
                .eq(Shop::getUserId, userId)
                .in(Shop::getShopMode, shopModes)
                .orderByDesc(com.medusa.gruul.common.mp.model.base.BaseEntity::getCreateTime)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        if (shop == null) {
            return null;
        }
        return ShopAuditVO.auditFormShop(shop);
    }


    @Lazy
    @Autowired
    public void setGoodsRpcService(GoodsRpcService goodsRpcService) {
        this.goodsRpcService = goodsRpcService;
    }

    @Lazy
    @Autowired
    public void setOrderRpcService(OrderRpcService orderRpcService) {
        this.orderRpcService = orderRpcService;
    }
}
