package com.medusa.gruul.addon.rebate.model.bo;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author 张治保
 * @since 2023/9/14
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RebateCalculation implements Serializable {

    /**
     * 计算表达式
     */
    private String expression;

    /**
     * 计算结果
     */
    private Long result;

    /**
     * 返利计算逻辑
     */
    public static RebateCalculation rebateCalculation(Long serviceFee, BigDecimal rebateRate) {
        //预计返利
        long result = AmountCalculateHelper.getDiscountAmount(
                serviceFee,
                rebateRate
        );
        //返利计算表达式
        String expression = StrUtil.format(
                "{} * {} = {}",
                AmountCalculateHelper.toYuan(serviceFee),
                rebatePercentStr(rebateRate),
                AmountCalculateHelper.toYuan(result)
        );
        return new RebateCalculation().setExpression(expression)
                .setResult(result);
    }

    public static String rebatePercentStr(BigDecimal rebateRate) {
        return rebateRate.multiply(CommonPool.BIG_DECIMAL_UNIT_CONVERSION_HUNDRED).setScale(2, RoundingMode.DOWN).toPlainString() + "%";
    }


}
