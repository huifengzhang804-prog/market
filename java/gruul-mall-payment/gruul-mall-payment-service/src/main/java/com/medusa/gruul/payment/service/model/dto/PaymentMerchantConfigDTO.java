package com.medusa.gruul.payment.service.model.dto;

import com.medusa.gruul.common.system.model.model.Platform;
import lombok.Data;

/**
 *
 * 商户配置支付平台dto
 *
 * @author xiaoq
 * @ Description 商户配置支付平台dto
 * @date 2022-07-13 10:51
 */
@Data
public class PaymentMerchantConfigDTO {


    /**
     * 商户渠道配置表id
     */
    private String detailsId;


    /**
     * 可支付平台类型
     */
    private Platform platform;
}
