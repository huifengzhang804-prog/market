package com.medusa.gruul.payment.api.model.param;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.payment.api.enums.FeeType;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * date 2023/6/5
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CombineOrderParam implements Serializable {

    /**
     * 主订单号
     */
    @NotBlank
    private String orderNo;

    /**
     * 支付类型
     */
    @NotNull
    private PayType payType;


    /**
     * 支付超时时间 单位秒
     */
    @NotNull
    private Long seconds;

    /**
     * 附加信息
     */
    private String attach;

    /**
     * 消费者用户信息
     */
    @NotNull
    @Valid
    private Consumer consumer;

    /**
     * 子订单号
     */
    @NotNull
    @Size(min = 1, max = 6)
    private List<SubOrder> subOrders;


    /**
     * rabbitmq交换机 用户支付成功回调
     */
    @NotBlank
    private String exchange;

    /**
     * rabbitmq路由key 用于支付成功回调
     */
    @NotBlank
    private String routeKey;

    /**
     * 转成 普通支付信息
     *
     * @return PaymentInfoDTO
     */
    public PaymentInfoDTO toPaymentInfo() {
        PaymentInfoDTO pay = new PaymentInfoDTO();
        pay.setOrderNum(this.orderNo);
        pay.setPayType(this.payType);
        pay.setSubject(StrUtil.format("订单编号：{}", pay.getOrderNum()));
        pay.setBody("商品店铺合计:" + this.subOrders.size() + "个");
        pay.setExchange(this.exchange);
        pay.setRouteKey(this.routeKey);
        pay.setFeeType(this.subOrders.get(0).getCurrency());
        pay.setTerminalIp(this.consumer.clientIp);
        pay.setTotalFee(this.subOrders.stream().mapToLong(SubOrder::getTotalAmount).sum());
        pay.setPayPlatform(this.consumer.platform);
        pay.setSeconds(this.seconds);
        pay.setUserId(this.consumer.userId);
        pay.setOpenId(this.consumer.openId);
        pay.setAttach(this.attach);
        pay.setShopId(0L);
        return pay;
    }

    /**
     * 消费者用户信息
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Consumer implements Serializable {

        /**
         * 支付平台类型
         */
        @NotNull
        private Platform platform;

        /**
         * 用户id
         */
        @NotNull
        private Long userId;

        /**
         * 用户openId 非必填  小程序支付必填
         */
        private String openId;

        /**
         * 设备id
         */
        @NotBlank
        private String deviceId;

        /**
         * 客户端ip
         */
        @NotBlank
        private String clientIp;


    }

    /**
     * 子订单信息
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class SubOrder implements Serializable {
        /**
         * 店铺id
         */
        private Long shopId;

        /**
         * 子订单号
         */
        private String subOrderNo;

        /**
         * 子订单支付金额
         */
        private Long totalAmount;

        /**
         * 子订单支付币种类型
         */
        private FeeType currency;

        /**
         * 交易描述信息
         * 例子: 可以设置为店铺名-商品数量
         * 如: 一号店-2件商品
         */
        private String description;

        /**
         * 是否可以分账
         */
        private Boolean profitSharing;

        /**
         * 补差金额 平台优惠扣除的金额 可以用作补差金额 最后可分账额度为 totalAmount + subsidyAmount
         */
        private Long subsidyAmount;

    }

}
