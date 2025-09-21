package com.medusa.gruul.addon.bargain.model.dto;


import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.medusa.gruul.addon.bargain.model.BargainErrorCode;
import com.medusa.gruul.addon.bargain.model.enums.ActivityStatus;
import com.medusa.gruul.addon.bargain.model.enums.HelpCutAmountType;
import com.medusa.gruul.addon.bargain.mp.entity.Bargain;
import com.medusa.gruul.addon.bargain.mp.entity.BargainProduct;
import com.medusa.gruul.addon.bargain.mp.service.IBargainProductService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainService;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 砍价活动DTO
 *
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BargainDTO implements BaseDTO {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    @NotBlank(message = "店铺名称不能为空")
    private String shopName;

    /**
     * 砍价活动名称
     */
    @NotBlank(message = "活动名称限15字")
    private String name;

    /**
     * 活动开始日期
     */
    private LocalDateTime startTime;

    /**
     * 活动结束日期
     */
    @Future
    private LocalDateTime endTime;

    /**
     * 砍价人数
     */
    @NotNull
    @Min(2)
    private Integer bargainingPeople;

    /**
     * 砍价有效期
     */
    @NotNull
    @Min(5)
    private Integer bargainValidityPeriod;

    /**
     * 是否自我砍价
     */
    private Boolean isSelfBargain;

//    /**
//     * 用户类型
//     */
//    private UserType userType;

    /**
     * 活动预热时间
     */
    @Min(0)
    @Max(5)
    private Integer activityPreheat;

    /**
     * 是否可叠加优惠（会员，优惠券，满减）
     */
    private StackableDiscount stackable;

    /**
     * 活动状态
     */
    private ActivityStatus status;

    /**
     * 帮砍金额
     */
    @NotNull
    private HelpCutAmountType helpCutAmount;

    /**
     * 砍价活动商品
     */
    @Valid
    @NotNull
    @Size(min = 1)
    private List<BargainProductDTO> bargainProducts;

    /**
     * 活动商品数
     */
    private Integer productNum;


    /**
     * 新增砍价活动
     *
     * @param shopId 店铺id
     */
    public Bargain newBargain(Long shopId) {
        return new Bargain()
                .setName(name)
                .setShopId(shopId)
                .setShopName(shopName)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setBargainingPeople(bargainingPeople)
                .setBargainValidityPeriod(bargainValidityPeriod)
                .setIsSelfBargain(isSelfBargain)
//                .setUserType(userType)
//                .setActivityPreheat(activityPreheat != null ? activityPreheat : CommonPool.NUMBER_ZERO)
                .setStackable(stackable)
                .setStatus(ActivityStatus.NOT_STARTED)
                .setHelpCutAmount(helpCutAmount)
                .setPayOrder(CommonPool.NUMBER_ZERO)
                .setPeopleNum(CommonPool.NUMBER_ZERO)
                .setProductNum(productNum)
                .setAmountReceivable(0L);
    }

    /**
     * 校验参数
     *
     * @param shopId              店铺id
     * @param bargainService      砍价活动服务
     * @param brandProductService 砍价活动商品服务
     */
    public void validParam(Long shopId, IBargainService bargainService, IBargainProductService brandProductService) {
        if (startTime.isAfter(endTime)) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "开始时间不能小于结束时间");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            // 如果开始时间小于当前时间 重新设置开始时间为当前时间+1分钟
            startTime = LocalDateTime.now().plusMinutes(CommonPool.NUMBER_ONE);
        }

        // 校验活动时间是否存在同一商品
        List<Bargain> bargains = bargainService.lambdaQuery()
                .eq(Bargain::getShopId, shopId)
//                .eq(ClientType.PLATFORM_CONSOLE.equals(ISystem.clientTypeMust()),Bargain::getPlatformDeleteFlag,Boolean.FALSE)
//                .eq(!ClientType.PLATFORM_CONSOLE.equals(ISystem.clientTypeMust()),Bargain::getShopDeleteFlag,Boolean.FALSE)
                .notIn(Bargain::getStatus,
                        Lists.newArrayList(ActivityStatus.ILLEGAL_SELL_OFF, ActivityStatus.SHOP_SELL_OFF))
                .not(wrapper -> wrapper.gt(Bargain::getStartTime, endTime)
                        .or()
                        .lt(Bargain::getEndTime, startTime)
                ).list();
        Set<Long> productIds = bargainProducts.stream().map(BargainProductDTO::getProductId).collect(Collectors.toSet());
        // 设置活动商品数
        setProductNum(productIds.size());
        if (CollUtil.isNotEmpty(bargains)) {
            Set<Long> bargainIds = bargains.stream().map(Bargain::getId).collect(Collectors.toSet());
            // 校验活动时间内是否存在相同商品
            boolean exists = brandProductService.lambdaQuery()
                    .eq(BargainProduct::getShopId, shopId)
                    .in(BargainProduct::getProductId, productIds)
                    .in(BargainProduct::getActivityId, bargainIds)
                    .exists();
            if (exists) {
                throw new GlobalException(BargainErrorCode.BARGAIN_PRODUCT_EXISTS, "当前活动时间内已存在下述商品");
            }
        }

        for (BargainProductDTO bargainProduct : bargainProducts) {
            if (bargainProduct.getFloorPrice() >= bargainProduct.getSkuPrice()) {
                throw new GlobalException("砍价底价需小于实售价");
            }
        }
    }

    /**
     * 新增砍价活动商品
     *
     * @param shopId     店铺id
     * @param activityId 砍价活动id
     * @return 砍价活动商品
     */
    public List<BargainProduct> newBargainProduct(Long shopId, Long activityId) {
        return bargainProducts.stream().map(bargainProductDTO -> new BargainProduct()
                .setProductId(bargainProductDTO.getProductId())
                .setActivityId(activityId)
                .setShopId(shopId)
                .setProductName(bargainProductDTO.getProductName())
                .setProductPic(bargainProductDTO.getProductPic())
                .setSkuId(bargainProductDTO.getSkuId())
                .setSkuName(bargainProductDTO.getSkuName())
                .setStock(bargainProductDTO.getStock())
                .setSkuStock(bargainProductDTO.getSkuStock())
                .setStockType(bargainProductDTO.getStockType())
                .setSkuPrice(bargainProductDTO.getSkuPrice())
                .setFloorPrice(bargainProductDTO.getFloorPrice())
        ).toList();

    }
}
