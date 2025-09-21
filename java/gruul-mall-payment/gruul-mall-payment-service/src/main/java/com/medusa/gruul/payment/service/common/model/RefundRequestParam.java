package com.medusa.gruul.payment.service.common.model;

import com.medusa.gruul.payment.api.model.dto.RefundRequestDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/12/12
 */
@Getter
@Setter
@Accessors(chain = true)
public class RefundRequestParam implements Serializable {

    /**
     * 退款请求参数
     */
    private RefundRequestDTO request;

    /**
     * 支付使用的商户配置id
     */
    private String detailsId;

    /**
     * 支付回调参数
     */
    private String notifyParam;
}
