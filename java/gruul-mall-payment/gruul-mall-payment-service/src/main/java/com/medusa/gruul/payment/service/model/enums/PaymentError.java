package com.medusa.gruul.payment.service.model.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2023/6/25
 */
@Getter
@RequiredArgsConstructor
public enum PaymentError implements Error {

    /**
     * 退款金额不可高于支付金额
     */
    REFUND_AMOUNT_CANNOT_GREATER_THAN_PAYMENT_AMOUNT(60000, "payment.refund.amount.cannot.greater.than.payment.amount"),

    /**
     * 订单退款时发生错误
     */
    ORDER_REFUND_ERROR(60001, "payment.order.refund.error"),

    /**
     * 合单支付失败
     */
    COMBINE_ORDER_ERROR(60002, "payment.combine.order.error"),

    /**
     * 缺少网站授权函
     */
    MISS_WEBSITE_AUTHORIZATION_LETTER(60003, "payment.miss.website.authorization.letter"),

    /**
     * 特约商户申请失败
     */
    SUB_MERCHANT_APPLY_ERROR(60004, "payment.sub.merchant.apply.error"),

    /**
     * 特约商户申请查询失败
     */
    SUB_MERCHANT_APPLY_QUERY_ERROR(60005, "payment.sub.merchant.apply.query.error"),

    /**
     * 图片素材上传失败
     */
    IMAGE_MATERIAL_UPLOAD_ERROR(60006, "payment.image.material.upload.error"),

    /**
     * 解冻剩余资金失败
     */
    UNFREEZE_REMAINING_FUNDS_ERROR(60007, "payment.unfreeze.remaining.funds.error"),

    /**
     * 添加分账方失败
     */
    ADD_PROFIT_SHARING_RECEIVER_ERROR(60008, "payment.add.profit.sharing.receiver.error"),

    /**
     * 退款申请失败
     */
    REFUND_APPLY_ERROR(60009, "payment.refund.apply.error"),

    /**
     * 未查询到支付记录
     */
    NOT_FOUND_PAYMENT_RECORD(60010, "payment.not.found.payment.record"),

    /**
     * 支付证书上传失败
     */
    PAYMENT_CERTIFICATE_UPLOAD_ERROR(60011, "payment.certificate.upload.error"),

    /**
     * 支付证书格式不正确
     */
    PAYMENT_CERTIFICATE_FORMAT_ERROR(60012, "payment.certificate.format.error"),

    /**
     * 支付渠道未配置
     */
    PAYMENT_CHANNEL_NOT_CONFIGURED(60013, "payment.channel.not.configured"),

    /**
     * 用户余额不足
     */
    USER_BALANCE_NOT_ENOUGH(60014, "payment.user.balance.not.enough"),

    /**
     * 支付状态查询异常
     */
    PAYMENT_STATUS_QUERY_ERROR(60015, "payment.status.query.error"),

    /**
     * 同一种支付渠道不能同时存在
     */
    PAYMENT_CHANNEL_DUPLICATE(60016, "payment.channel.duplicate"),
    ;

    private final int code;
    private final String msgCode;

    @Override
    public int code() {
        return getCode();
    }

    @Override
    public String msg() {
        return I18N.msg(getMsgCode());
    }
}
