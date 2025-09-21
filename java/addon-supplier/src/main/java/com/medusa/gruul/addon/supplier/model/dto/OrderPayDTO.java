package com.medusa.gruul.addon.supplier.model.dto;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.supplier.model.enums.OrderPayType;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author 张治保
 * date 2023/7/25
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderPayDTO implements BaseDTO {
    /**
     * 订单号
     */
    @NotBlank
    private String orderNo;

    /**
     * 支付方式
     */
    @NotNull
    private OrderPayType payType;

    /**
     * 支付凭证
     */
    private List<String> proof;

    @Override
    public void validParam() {
        if (OrderPayType.OFFLINE == getPayType()) {
            if (CollectionUtil.isEmpty(proof)||proof.size()> CommonPool.NUMBER_THREE) {
                throw SystemCode.PARAM_VALID_ERROR.exception();
            }

        }
    }
}
