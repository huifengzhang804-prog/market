package com.medusa.gruul.payment.api.model.dto;

import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.web.valid.annotation.Price;
import com.medusa.gruul.payment.api.enums.FeeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 支付信息DTO
 *
 * @author xiaoq
 * @ Description PaymentInfoDTO.java
 * @date 2022-07-25 14:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5034862358182558743L;

    /**
     * 业务订单唯一标识
     */
    @NotNull
    private String orderNum;


    /**
     * 订单支付类型
     */
    @NotNull
    private PayType payType;


    /**
     * 支付标题
     */
    @NotNull
    private String subject;


    /**
     * 针对交易具体描述
     */
    @NotNull
    private String body;


    /**
     * rabbitmq路由key 支付成功回调
     */
    @NotNull
    private String routeKey;

    /**
     * rabbitmq交换机
     */
    @NotNull
    private String exchange;


    /**
     * 支付货币类型 默认 人民币
     */
    @NotNull
    private FeeType feeType;

    /**
     * 终端ip
     */
    @NotNull
    private String terminalIp;

    /**
     * 订单支付总价格(毫)
     */
    @NotNull
    @Min(100L)
    @Price()
    private Long totalFee;

    /**
     * 支付平台类型
     */
    @NotNull
    private Platform payPlatform;


    /**
     * 业务支付超时时间 单位秒
     */
    private Long seconds;

    /**
     * 支付用户id
     */
    private Long userId;

    /**
     * 支付用户微信openId
     */
    private String openId;

    /**
     * 预留json数据
     */
    private String attach;

    /**
     * 商户id
     */
    @NotNull
    private Long shopId;

    /**
     * 付款条码串,人脸凭证，有关支付代码相关的，
     */
    private String authCode;
    /**
     * 微信专用，，，，
     * WAP支付链接
     */
    private String wapUrl;
    /**
     * 微信专用，，，，
     * WAP支付网页名称
     */
    private String wapName;

}
