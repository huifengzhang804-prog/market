package com.medusa.gruul.addon.matching.treasure.model.dto;


import com.medusa.gruul.addon.matching.treasure.model.MatchingTreasureErrorCode;
import com.medusa.gruul.addon.matching.treasure.model.enums.SetMealStatus;
import com.medusa.gruul.addon.matching.treasure.model.enums.SetMealType;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMeal;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
public class SetMealDTO {

    /**
     * 套餐id
     */
    private Long setMealId;


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
     * 套餐名称
     */
    @Size(max = 15)
    @NotBlank(message = "套餐名称不能为空")
    private String setMealName;

    /**
     * 套餐描述
     */
    @Size(max = 40)
    @NotBlank(message = "套餐描述不能为空")
    private String setMealDescription;

    /**
     * 套餐主图
     */
    @NotNull
    private String setMealMainPicture;

    /**
     * 套餐类型 [0:自选商品套餐 1:固定组合套餐]
     */
    @NotNull
    private SetMealType setMealType;

    /**
     * 活动状态 [0:未开始 1:进行中 2:已结束 3:违规下架]
     */
    private SetMealStatus setMealStatus;

    /**
     * 开始时间
     */
    @NotNull
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Future
    @NotNull
    private LocalDateTime endTime;

    /**
     * 参与人数
     */
    private Integer peopleNum;

    /**
     * 应收金额
     */
    private Long amountReceivable;

    /**
     * 套餐配送类型
     */
    private DistributionMode distributionMode;

    /**
     * 是否可叠加优惠（会员，优惠券，满减）
     */
    private StackableDiscount stackable;

    /**
     * 主商品
     */
    @Valid
    @NotNull
    @Size(min = 1)
    private List<SetMealProductDTO> mainProduct;
    /**
     * 搭配商品
     */
    @Valid
    @NotNull
    @Size(min = 1)
    private List<SetMealProductDTO> matchingProducts;


    public void validParams() {
        if (startTime.isAfter(endTime)) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "开始时间需小于结束时间");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            // 如果开始时间小于当前时间 重新设置开始时间为当前时间+1分钟
            startTime = LocalDateTime.now().plusMinutes(CommonPool.NUMBER_ONE);
        }
        Set<Long> mainProductIds = mainProduct.stream().map(SetMealProductDTO::getProductId).collect(Collectors.toSet());
        Set<Long> matchingProductIds = matchingProducts.stream().map(SetMealProductDTO::getProductId).collect(Collectors.toSet());
        // 存在交集
        if (mainProductIds.stream().anyMatch(matchingProductIds::contains)) {
            throw new GlobalException(MatchingTreasureErrorCode.SET_MEAL_PRODUCT_REPEAT, "套餐主商品和搭配商品不能重复");
        }
        Set<Long> skuIds = matchingProducts.stream().map(SetMealProductDTO::getSkuId)
            .collect(Collectors.toSet());
        if (matchingProducts.size() != skuIds.size()){
            throw new GlobalException("搭配商品存在重复");
        }
        if (mainProductIds.size() > CommonPool.NUMBER_ONE || matchingProductIds.size() > CommonPool.NUMBER_FOUR) {
            throw new GlobalException(MatchingTreasureErrorCode.SET_MEAL_PRODUCT_OUT_OF_RANGE, "套餐主商品只能有1个,搭配商品只能有4个");
        }
    }

    public SetMeal newSetMeal(Long shopId, long setMealId) {
        SetMeal setMeal = new SetMeal()
                .setSetMealName(setMealName)
                .setShopId(shopId)
                .setShopName(shopName)
                .setSetMealStatus(SetMealStatus.NOT_STARTED)
                .setSetMealDescription(setMealDescription)
                .setSetMealMainPicture(setMealMainPicture)
                .setStackable(stackable)
                .setSetMealType(setMealType)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setPeopleNum(CommonPool.NUMBER_ZERO)
                .setDistributionMode(distributionMode)
                .setAmountReceivable(0L);
        setMeal.setId(setMealId);
        mainProduct.addAll(matchingProducts);
        return setMeal;

    }
}
