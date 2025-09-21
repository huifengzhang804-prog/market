package com.medusa.gruul.user.api.model.vo;

import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.api.enums.RightsType;
import com.medusa.gruul.user.api.model.dto.MemberLabelDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * 会员聚合信息VO
 *
 * @author xiaoq
 * @Description MemberAggregationInfoVO.java
 * @date 2022-11-15 15:12
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MemberAggregationInfoVO implements Serializable {
    /**
     * 会员折扣最大值 1000
     */
    public static final long MAX_VIP_DISCOUNT_RATE = 1000;

    /**
     * 用户头像
     */
    private String userHeadPortrait;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户成长值
     */
    private Long growthValue;

    /**
     * 用户所使用得会员卡类型
     */
    private MemberType memberType;

    /**
     * 当前所使用得会员VO
     */
    private CurrentMemberVO currentMemberVO;

    /**
     * 下级会员所需成长值
     */
    private Long subordinateGrowthValue;

    /**
     * 会员价标签
     */
    private MemberLabelDTO memberLabel;


    /**
     * 计算当前会员折扣
     *
     * @param amount 商品金额
     * @return 会员折扣金额
     */
    public long memberDiscountAmount(Long amount) {
        long min = CommonPool.NUMBER_ZERO;
        if (currentMemberVO == null) {
            return min;
        }
        Map<RightsType, List<RelevancyRightsVO>> relevancyRights = currentMemberVO.getRelevancyRights();
        if (MapUtil.isEmpty(relevancyRights)) {
            return min;
        }

        List<RelevancyRightsVO> goodsRights = relevancyRights.get(RightsType.GOODS_DISCOUNT);
        if (goodsRights == null) {
            return min;
        }
        // 商品折扣
        Long extendValue = goodsRights.get(0).getExtendValue();
        if (extendValue == null || extendValue <= 0) {
            return min;
        }
        if (extendValue >= MAX_VIP_DISCOUNT_RATE) {
            return amount;
        }
        return AmountCalculateHelper.getDiscountAmount(
                amount,
                MAX_VIP_DISCOUNT_RATE - extendValue,
                MAX_VIP_DISCOUNT_RATE
        );
    }

    /**
     * 计算当前会员折扣 和这就描述
     *
     * @param amount 商品金额
     * @return 会员折扣金额
     */
    public ProductDiscount memberProductDiscount(Long amount) {
        ProductDiscount discount = new ProductDiscount()
                .setDiscountAmount(CommonPool.NUMBER_ZERO);
        if (currentMemberVO == null) {
            return discount;
        }
        Map<RightsType, List<RelevancyRightsVO>> relevancyRights = currentMemberVO.getRelevancyRights();
        if (MapUtil.isEmpty(relevancyRights)) {
            return discount;
        }

        List<RelevancyRightsVO> goodsRights = relevancyRights.get(RightsType.GOODS_DISCOUNT);
        if (goodsRights == null) {
            return discount;
        }
        // 商品折扣
        Long extendValue = goodsRights.get(0).getExtendValue();
        if (extendValue == null) {
            return discount;
        }
        if (extendValue > MAX_VIP_DISCOUNT_RATE) {
            return discount;
        }
        if (extendValue <= 0) {
            return discount.setDiscountAmount(amount)
                    .setDiscountDesc("0.0折");
        }
        return discount
                .setDiscountAmount(
                        AmountCalculateHelper.getDiscountAmount(
                                amount,
                                MAX_VIP_DISCOUNT_RATE - extendValue,
                                MAX_VIP_DISCOUNT_RATE
                        )
                )
                .setDiscountDesc(
                        new BigDecimal(extendValue)
                                .divide(CommonPool.BIG_DECIMAL_UNIT_CONVERSION_HUNDRED, 2, RoundingMode.DOWN)
                                .stripTrailingZeros()
                                .toPlainString() + "折"

                );
    }

    @Getter
    @Setter
    @ToString
    @Accessors(chain = true)
    public static class ProductDiscount implements Serializable {
        /**
         * 商品折扣金额
         */
        private long discountAmount;

        /**
         * 折扣描述
         */
        private String discountDesc;
    }


}
