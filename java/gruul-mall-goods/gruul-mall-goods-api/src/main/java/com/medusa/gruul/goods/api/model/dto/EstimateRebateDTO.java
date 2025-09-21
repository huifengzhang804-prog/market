package com.medusa.gruul.goods.api.model.dto;

import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 张治保
 * @since 2023/9/15
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class EstimateRebateDTO implements Serializable {

    /**
     * 服务费
     */
    @NotNull
    @Min(0)
    private Long serviceFeePercent;

    /**
     * 商品金额
     */
    @NotNull
    @Min(0)
    private Long amount;

    /**
     * sku 销售价
     */
    private Long skuSalePrice;

    /**
     * 商品销售类型
     */
    private SellType sellType;

    /**
     * 获取服务费真实比例
     *
     * @return 服务费真实比例
     */
    public BigDecimal serviceFeeRate() {
        return AmountCalculateHelper.getDiscountRate(serviceFeePercent, CommonPool.UNIT_CONVERSION_HUNDRED);
    }
}
