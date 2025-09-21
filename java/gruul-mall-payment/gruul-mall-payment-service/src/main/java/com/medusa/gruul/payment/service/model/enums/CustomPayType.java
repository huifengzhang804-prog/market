package com.medusa.gruul.payment.service.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.egzosn.pay.common.bean.TransactionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 自定义支付类型
 *
 * @author xiaoq
 * @Description 自定义支付类型
 * @date 2022-10-11 16:08
 */
@Getter
@RequiredArgsConstructor
public enum CustomPayType implements TransactionType {


    /**
     *
     */
    BALANCE_PAID("balance"),
    ;


    /**
     * 获取交易类型
     *
     * @return 交易类型
     */
    @Override
    public String getType() {
        return null;
    }

    /**
     * 获取接口
     *
     * @return 接口
     */
    @Override
    public String getMethod() {
        return null;
    }


    @EnumValue
    private final String type;

}
