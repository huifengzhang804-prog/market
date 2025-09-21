package com.medusa.gruul.payment.api.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.payment.api.enums.FeeType;
import com.medusa.gruul.payment.api.enums.NotifyStatus;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.payment.api.enums.TradeStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 *
 * 业务支付信息表
 *
 * @author xiaoq
 * @ Description 业务支付信息表
 * @date 2022-07-25 13:41
 */

@Data
@Accessors(chain = true)
@TableName(value = "t_payment_info", excludeProperty = "deleted")
@EqualsAndHashCode(callSuper = true)
public class PaymentInfo extends BaseEntity {

    /**
     * 商户隔离id
     */
    private Long shopId;

    /**
     * 微信用户open_id
     */
    private String openId;

    /**
     * 用户系统id
     */
    private Long userId;

    /**
     * 微信小程序id
     */
    private String appId;

    /**
     * 业务订单唯一标识
     */
    private String orderNum;

    /**
     * 支付渠道
     */
    private PayType payType;

    /**
     * 支付平台类型
     */
    private Platform payPlatform;

    /**
     * 支付标题
     */
    private String subject;

    /**
     * 针对交易具体描述
     */
    private String body;

    /**
     * rabbitmq路由key
     */
    private String routeKey;

    /**
     * rabbitmq 交换机
     */
    private String exchange;

    /**
     * 支付货币类型 默认 人民币
     */
    private FeeType feeType;

    /**
     * 订单支付总价格(毫)
     */
    private Long totalFee;

    /**
     * 订单支付超时时间 单位秒
     */
    private String seconds;

    /**
     * 预留json数据
     */
    private String attach;

    /**
     * 终端ip
     */
    private String terminalIp;

    /**
     * 支付流水号(支付平台返回)
     */
    private String transactionId;

    /**
     * 业务回调地址
     */
    private String businessNotifyUrl;

    /**
     * 第三方回调是否已处理 0-未处理 1-已处理
     */
    private NotifyStatus thirdPartyNotifyStatus;

    /**
     * 业务方是否已正确处理  0-未处理 1-已处理
     */
    private NotifyStatus businessNotifyStatus;

    /**
     * 交易状态(交易状态：1（交易创建，等待买家付款）、2（未付款交易超时关闭）、3（交易支付成功)
     */
    private TradeStatus tradeStatus;

    /**
     * 回调次数
     */
    private Integer thirdPartyNotifyNumber;

    /**
     * 附加数据,格式为json字符串,怎么发送怎么返回
     */
    private String businessParams;

}