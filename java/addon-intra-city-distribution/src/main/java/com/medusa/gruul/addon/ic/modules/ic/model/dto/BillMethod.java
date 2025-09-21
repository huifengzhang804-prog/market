package com.medusa.gruul.addon.ic.modules.ic.model.dto;

import com.medusa.gruul.addon.ic.modules.ic.model.enums.BillType;
import com.medusa.gruul.common.web.valid.annotation.Price;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 配送费 计费方式
 *
 * @author 张治保
 * @since 2024/8/13
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BillMethod implements Serializable {

    /**
     * 计费方式
     */
    @NotNull
    private BillType type;

    /**
     * 免配送费额度
     * 已距离计算 单位公里（km）
     * 已重量计算 单位公斤（kg）
     */
    @DecimalMin("0.00")
    private BigDecimal free;

    /**
     * 步长 超出免费额度 每 step 增加配送费 stepPrice
     * 已距离计算 单位公里（km）
     * 已重量计算 单位公斤（kg）
     */
    @DecimalMin("0.01")
    private BigDecimal step;

    /**
     * 步长对应的配送费
     */
    @Min(0)
    @Price
    private Long stepPrice;

    /**
     * 计算 当前计费方式产出的费用
     *
     * @param total 总距离｜总重量
     * @return 当前计费方式产出的费用
     */
    public Long billMethodPrice(BigDecimal total) {
        //如果当前免费额度 大于等于 总距离｜总重量 则不产生额外费用
        //或每步费用为0 也不产生额外费用
        if (free.compareTo(total) >= 0 || stepPrice == 0) {
            return 0L;
        }
        //收费部分
        BigDecimal notFreePart = total.subtract(free);
        //收费部分 小于等于 0 则处于免费区间
        if (notFreePart.compareTo(BigDecimal.ZERO) <= 0) {
            return 0L;
        }
        //总步数 (total-free)/step 向上取整
        long totalStep = notFreePart.divide(step, 0, RoundingMode.UP)
                .longValue();
        //总步数 * 每步费用
        return totalStep * stepPrice;
    }

}
