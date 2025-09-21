package com.medusa.gruul.addon.full.reduction.model.bo;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.full.reduction.model.enums.FullReductionRuleType;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 满减折扣
 *
 * @author wudi
 */
@Getter
@Setter
@Accessors(chain = true)
public class FullReductionRule implements Serializable {

    /**
     * 0:满减 1:满折
     */
    @NotNull
    private FullReductionRuleType type;

    /**
     * 满减金额/满折金额
     */
    @NotNull
    private Long conditionAmount;

    /**
     * 优惠金额
     */
    private Long discountAmount;

    /**
     * 折扣比 0.1-9.9
     */
    private BigDecimal discountRatio;


    /**
     * 获取当前满减规则描述信息
     *
     * @return 描述信息
     */
    public String getDesc() {
        if (FullReductionRuleType.FULL_REDUCTION == type) {
            return StrUtil.format("满{}元减{}", AmountCalculateHelper.toYuan(conditionAmount).toPlainString(),
                    AmountCalculateHelper.toYuan(discountAmount).toPlainString());
        }
        return StrUtil.format("满{}元{}折", AmountCalculateHelper.toYuan(conditionAmount).toPlainString(),
                discountRatio.toPlainString());
    }


    /**
     * 获取满减优惠金额
     *
     * @param amount 订单金额
     * @return 优惠金额
     */
    public long getDiscountAmount(Long amount) {
        if (amount < conditionAmount) {
            return 0L;
        }
        long curDiscountAmount = FullReductionRuleType.FULL_REDUCTION == type ?
                discountAmount :
                AmountCalculateHelper.getDiscountAmount(
                        amount,
                        BigDecimal.TEN.subtract(discountRatio)
                                .divide(BigDecimal.TEN, CommonPool.NUMBER_EIGHT, RoundingMode.DOWN)
                );

        if (curDiscountAmount < 0) {
            return 0L;
        }
        return Math.min(amount, curDiscountAmount);
    }

}
