package com.medusa.gruul.payment.api.model.transfer;

import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * 转账基础转信息
 *
 * @author 张治保
 * date 2022/11/25
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public abstract class TransferParam implements BaseDTO {

    /**
     * 外部订单号 需要保证唯一
     */
    @NotBlank
    private String outNo;

    /**
     * 转账金额
     */
    @NotNull
    @Min(100)
    private Long amount;

    /**
     * 转账备注信息
     */
    private String remark;

    /**
     * 转账金额的支付类型
     *
     * @return 支付类型
     */
    public abstract PayType getPayType();

    /**
     * 获取decimal 价格 单位：员 精确到分 小数点后两位
     */
    public BigDecimal getAmountDecimal() {
        Long amount = getAmount();
        if (amount == null || amount <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(amount)
                .divide(
                        CommonPool.BIG_DECIMAL_UNIT_CONVERSION_TEN_THOUSAND,
                        2,
                        RoundingMode.DOWN
                );
    }
}
