package com.medusa.gruul.addon.coupon.addon.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.coupon.addon.AddonCouponProvider;
import com.medusa.gruul.addon.coupon.model.CouponErrorCode;
import com.medusa.gruul.addon.coupon.model.dto.ProductCouponPageDTO;
import com.medusa.gruul.addon.coupon.model.enums.CouponProductType;
import com.medusa.gruul.addon.coupon.model.enums.CouponType;
import com.medusa.gruul.addon.coupon.model.vo.CouponVO;
import com.medusa.gruul.addon.coupon.mp.entity.CouponOrderRecord;
import com.medusa.gruul.addon.coupon.mp.entity.CouponProduct;
import com.medusa.gruul.addon.coupon.mp.entity.CouponUser;
import com.medusa.gruul.addon.coupon.mp.service.ICouponOrderRecordService;
import com.medusa.gruul.addon.coupon.mp.service.ICouponService;
import com.medusa.gruul.addon.coupon.service.CouponPlusService;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.constant.SecurityConst;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.api.model.vo.CouponProductVO;
import com.medusa.gruul.goods.api.model.vo.GoodsCouponVO;
import com.medusa.gruul.goods.api.model.vo.ProductDiscountVO;
import com.medusa.gruul.order.api.addon.OrderAddonConstant;
import com.medusa.gruul.order.api.addon.coupon.CouponResponse;
import com.medusa.gruul.order.api.addon.coupon.OrderCouponParam;
import com.medusa.gruul.order.api.entity.OrderDiscount;
import com.medusa.gruul.order.api.enums.DiscountSourceStatus;
import com.medusa.gruul.order.api.enums.DiscountSourceType;
import com.medusa.gruul.search.api.addon.coupon.SearchAddonConstant;
import com.medusa.gruul.search.api.model.SearchCouponProductVO;
import com.medusa.gruul.search.api.model.SearchCouponVO;
import com.medusa.gruul.shop.api.addon.ShopAddonConstant;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;


/**
 * @author 张治保 date 2022/11/4
 */
@Service
@DubboService
@AddonProviders
@RequiredArgsConstructor
public class AddonCouponProviderImpl implements AddonCouponProvider {


    private final CouponPlusService couponPlusService;
    private final ICouponOrderRecordService couponOrderRecordService;
    private final ICouponService couponService;

    @Override
    @Log("订单使用优惠券")
    @AddonProvider(service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISCOUNT_SUPPORT_ID, methodName = "coupon")
    public CouponResponse useCoupon(OrderCouponParam orderCoupon) {
        Long buyerId = orderCoupon.getBuyerId();
        String orderNo = orderCoupon.getOrderNo();
        CouponResponse couponResponse = new CouponResponse();
        couponResponse.setBuyerId(buyerId)
                .setOrderNo(orderNo);

        Map<Long, Long> shopCoupons = orderCoupon.getShopCouponMap();
        if (CollUtil.isEmpty(shopCoupons)) {
            return couponResponse.setOrderDiscounts(Collections.emptyMap());
        }

        // 如果为平台券, 获取当前订单所包含的店铺id集合, 且添加平台代表的shopId(shopId=0)(平台端可以查看所有用券记录, 这里不添加也行)
        HashMap<Long, Set<Long>> orderShopIdsMap =
                null == shopCoupons.get(SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID) ?
                        new HashMap<>() :
                        new HashMap<>() {{
                            put(
                                    shopCoupons.get(SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID),
                                    Stream.of(orderCoupon.getShopProductAmountMap().keySet(),
                                                    Set.of(SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID))
                                            .flatMap(Collection::stream).collect(Collectors.toSet())
                            );
                        }};

        Boolean budget = orderCoupon.getBudget();
        Supplier<CouponResponse> supplierTask = () -> {
            Map<Long, Map<Long, Long>> shopProductAmountMap = orderCoupon.getShopProductAmountMap();
            Map<Long, AtomicLong> allStatisticsMap = this.toShopStatisticsMap(shopProductAmountMap);
            //添加平台的数据
            couponResponse.setOrderDiscounts(
                    shopCoupons.entrySet()
                            .stream()
                            .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            entry -> {
                                                Long shopId = entry.getKey();
                                                return couponPlusService.getCouponUserForUse(buyerId, shopId, entry.getValue())
                                                        .map(
                                                                couponUser -> this.validCouponUser(couponUser,
                                                                        shopProductAmountMap.get(shopId), allStatisticsMap)
                                                        )
                                                        .getOrElseThrow(() -> new GlobalException("优惠券不存在",
                                                                CouponErrorCode.COUPON_NOT_EXISTS,
                                                                new ShopProductSkuKey().setShopId(shopId)));
                                            }
                                    )
                            )
            );
            //非预计算 保存优惠券记录
            if (!budget && !couponOrderRecordService.save(
                    new CouponOrderRecord()
                            .setOrderNo(orderNo)
                            .setBuyerId(buyerId)
                            .setCoupons(shopCoupons)
            )) {
                throw new GlobalException(SystemCode.DATA_ADD_FAILED);
            }
            return couponResponse;
        };
        //如果是预计算 则直接返回
        if (budget) {
            return supplierTask.get();
        }
        //非预计算则批量锁定优惠券
        return couponPlusService.lockUserCouponsBatch(buyerId, shopCoupons, supplierTask, orderNo, orderShopIdsMap);
    }

    /**
     * 获取店铺前3个优先级高的优惠券规则 优先级：无门槛现金券 > 无门槛折扣券 > 满减券 > 满折券
     *
     * @param shopIds 店铺id
     */
    @Override
    @Log("取店铺前3个优先级高的优惠券规则")
    @AddonProvider(service = Services.GRUUL_MALL_SEARCH, supporterId = SearchAddonConstant.SEARCH_COUPON_SUPPORT_ID, methodName = "getCouponList")
    public List<SearchCouponVO> getCouponList(Set<Long> shopIds) {
        return getSearchCouponList(shopIds);
    }


    @Override
    @Log("取店铺前3个优先级高的优惠券规则")
    @AddonProvider(service = Services.GRUUL_MALL_GOODS, supporterId = SearchAddonConstant.SEARCH_COUPON_SUPPORT_ID, methodName = "getCouponListForProduct")
    public List<GoodsCouponVO> getCouponListForProduct(Set<Long> shopIds) {
        List<SearchCouponVO> searchCouponList = getSearchCouponList(shopIds);
        return searchCouponList.stream().map(searchCouponVO -> {
            List<CouponProductVO> include = searchCouponVO.getSearchCouponProductVOList()
                    .stream().map(searchCouponProductVO ->
                            new CouponProductVO().setProductId(searchCouponProductVO.getProductId())
                    ).collect(Collectors.toList());
            List<CouponProductVO> exclude = searchCouponVO.getExcludeCouponProductVOList()
                    .stream().map(searchCouponProductVO ->
                            new CouponProductVO().setProductId(searchCouponProductVO.getProductId())
                    ).collect(Collectors.toList());

            List<SearchCouponProductVO> excludeCouponProductVOList = searchCouponVO.getExcludeCouponProductVOList();
            return new GoodsCouponVO().setId(searchCouponVO.getId())
                    .setShopId(searchCouponVO.getShopId())
                    .setSearchCouponProductVOList(include)
                    .setExcludeCouponProductVOList(exclude)
                    .setSourceDesc(searchCouponVO.getSourceDesc());
        }).toList();
    }


    private List<SearchCouponVO> getSearchCouponList(Set<Long> shopIds) {
        List<CouponVO> couponList = couponPlusService.getCouponList(shopIds);
        if (CollUtil.isEmpty(couponList)) {
            return new ArrayList<>();
        }
        return handleCouponVO(couponList);
    }

    /**
     * 获取店铺前3个优先级高的优惠券规则 优先级：无门槛现金券 > 无门槛折扣券 > 满减券 > 满折券
     *
     * @param shopIds 店铺id
     */
    @Override
    @Log("取店铺前3个优先级高的优惠券规则")
    @AddonProvider(service = Services.GRUUL_MALL_SHOP, supporterId = ShopAddonConstant.SHOP_COUPON_SUPPORT_ID, methodName = "getShopCouponList")
    public List<SearchCouponVO> getShopCouponList(Set<Long> shopIds) {
        return getSearchCouponList(shopIds);
    }

    @Override
    @AddonProvider(filter = FILTER, service = Services.GRUUL_MALL_GOODS, supporterId = GoodsConstant.ADDON_SUPPORT_ID, methodName = "discount")
    public ProductDiscountVO discount(@Nullable Long userId, ShopProductKey key, Long amount) {
        ProductCouponPageDTO query = new ProductCouponPageDTO();
        query.setShopId(key.getShopId())
                .setProductId(key.getProductId())
                .setAmount(amount)
                .setCurrent(CommonPool.NUMBER_ONE)
                .setSize(CommonPool.NUMBER_FIVE);
        IPage<CouponVO> couponPage = couponService.productShopCouponPage(query, userId);
        List<CouponVO> records = couponPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return null;
        }
        return new ProductDiscountVO()
                .setDiscount(records.get(CommonPool.NUMBER_ZERO).getDiscountAmount())
                .setData(new JSONArray(records));
    }

    /**
     * 处理优惠券 VO
     *
     * @param couponList 优惠券
     * @return SearchCouponVO
     */
    private List<SearchCouponVO> handleCouponVO(List<CouponVO> couponList) {
        return couponList.stream().map(coupon -> {
            SearchCouponVO vo = new SearchCouponVO();
            vo.setId(coupon.getId());
            vo.setShopId(coupon.getShopId());
            vo.setRowNum(coupon.getRowNum());
            //对象转json
            vo.setData(new JSONObject(coupon));

            CouponProductType productType = coupon.getProductType();
            //优惠券描述名称
            CouponType type = coupon.getType();
            vo.setSourceDesc(
                    type.getConsumerDesc(coupon.getRequiredAmount(), coupon.getAmount(), coupon.getDiscount()));
            List<CouponProduct> couponProducts = coupon.getCouponProductList();
            if (CollUtil.isNotEmpty(couponProducts)) {

                if (CouponProductType.ASSIGNED.equals(productType)) {
                    //指定商品
                    vo.setSearchCouponProductVOList(coupon.copyProperties(couponProducts));
                }
                if (CouponProductType.ASSIGNED_NOT.equals(productType)) {
                    //排除商品
                    vo.setSearchCouponProductVOList(coupon.copyProperties(couponProducts));
                }

            }
            return vo;
        }).collect(Collectors.toList());
    }

    private OrderDiscount validCouponUser(CouponUser couponUser, Map<Long, Long> productAmountMap,
            Map<Long, AtomicLong> allStatisticsMap) {
        Long shopId = couponUser.getShopId();
        LocalDate now = LocalDate.now();
        //检查是否已使用
        if (couponUser.getUsed()) {
            throw new GlobalException("优惠券已被使用", CouponErrorCode.COUPON_USED,
                    new ShopProductSkuKey().setShopId(shopId));
        }
        //检查是否在有效期内
        if (now.isBefore(couponUser.getStartDate())) {
            throw new GlobalException("该优惠券暂不能使用", CouponErrorCode.COUPON_NOT_WORKING,
                    new ShopProductSkuKey().setShopId(shopId));
        }
        if (now.isAfter(couponUser.getEndDate())) {
            throw new GlobalException("该优惠券已失效", CouponErrorCode.COUPON_INVALID,
                    new ShopProductSkuKey().setShopId(shopId));
        }

        // 检查优惠券 商品类型
        CouponProductType productType = couponUser.getProductType();
        AtomicLong shopAmount = allStatisticsMap.get(shopId);
        Set<Long> targetProductIds = null;
        //指定商品生效
        if (productType.getIsAssigned()) {
            Tuple2<Set<Long>, AtomicLong> workingProductIdsAndTotalAmount = this.getWorkingProductIdsAndTotalAmount(
                    couponUser.getProductType(), couponUser.getProductIds(), productAmountMap);
            targetProductIds = workingProductIdsAndTotalAmount._1();
            if (CollUtil.isEmpty(targetProductIds)) {
                throw new GlobalException("不满足优惠券使用条件", CouponErrorCode.COUPON_WRONG_USE_CONDITION,
                        new ShopProductSkuKey().setShopId(shopId));
            }
            shopAmount = workingProductIdsAndTotalAmount._2;
        }
        //优惠券使用的实际总额
        long realTotalAmount = shopAmount.get();
        //检查是否满足门槛额度
        CouponType type = couponUser.getType();
        Long requiredAmount = couponUser.getRequiredAmount();
        if (type.getRequiredAmount() && realTotalAmount < requiredAmount) {
            throw new GlobalException("不满足优惠券使用条件", CouponErrorCode.COUPON_WRONG_USE_CONDITION,
                    new ShopProductSkuKey().setShopId(shopId));
        }
        BigDecimal discount = couponUser.getDiscount();
        Long amount = couponUser.getAmount();
        Long couponId = couponUser.getCouponId();
        return new OrderDiscount()
                .setSourceType(
                        shopId != SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID ? DiscountSourceType.SHOP_COUPON
                                : DiscountSourceType.PLATFORM_COUPON)
                .setSourceStatus(DiscountSourceStatus.OK)
                .setSourceId(couponId)
                .setSourceAmount(type.getDiscountAmount(realTotalAmount, amount, discount))
                .setTotalAmount(realTotalAmount)
                .setSourceDesc(type.getDesc(requiredAmount, amount, discount))
                .setProductIds(targetProductIds);
    }


    private Tuple2<Set<Long>, AtomicLong> getWorkingProductIdsAndTotalAmount(CouponProductType productType,
            Set<Long> couponUserProductIds, Map<Long, Long> productAmountMap) {
        Tuple2<Set<Long>, AtomicLong> result = Tuple.of(new HashSet<>(), new AtomicLong());
        productAmountMap.forEach(
                (productId, amount) -> {
                    //属于优惠券目标商品 指定生效则必须包含，指定不生效 则必须不包含
                    if ((CouponProductType.ASSIGNED == productType) == couponUserProductIds.contains(productId)) {
                        result._1().add(productId);
                        result._2().addAndGet(amount);
                    }
                }
        );
        return result;
    }

    /**
     * 遍历同及店铺信息 店铺总额与 店铺所有商品id
     *
     * @param shopProductAmount 店铺商品总价map
     * @return key：店铺id，平台我为0； value： 1。属于这个店铺的商品id集合 平台为空集合，2。属于这个店铺的商品总额，平台为所有店铺的总额
     */
    private Map<Long, AtomicLong> toShopStatisticsMap(Map<Long, Map<Long, Long>> shopProductAmount) {
        Map<Long, AtomicLong> amountMap = new HashMap<>(CommonPool.NUMBER_THIRTY);
        shopProductAmount.forEach(
                (shopId, productAmountMap) -> productAmountMap.forEach(
                        (productId, amount) -> {
                            AtomicLong shopAmount = amountMap.computeIfAbsent(shopId, (key) -> new AtomicLong());
                            shopAmount.addAndGet(amount);
                            AtomicLong platformAmount = amountMap.computeIfAbsent(
                                    SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID, (key) -> new AtomicLong());
                            platformAmount.addAndGet(amount);
                        }
                )
        );
        return amountMap;
    }
}
