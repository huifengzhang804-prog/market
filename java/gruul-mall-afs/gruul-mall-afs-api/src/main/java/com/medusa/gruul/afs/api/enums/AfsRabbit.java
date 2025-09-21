package com.medusa.gruul.afs.api.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.RequiredArgsConstructor;

/**
 * <p>
 *     售后mq枚举
 * </p>
 *
 * @author 张治保
 * date 2022/8/8
 */
@RequiredArgsConstructor
public enum AfsRabbit implements RabbitParent {
    /**
     * 系统自动同意售后申请
     */
    AFS_AUTO_AGREE_REQUEST("afs.auto.agreeRequest"),

    /**
     * 系统自动确认收到退货
     */
    AFS_AUTO_CONFIRM_RETURNED("afs.auto.confirmReturned"),

    /**
     * 系统自动关闭售后
     */
    AFS_AUTO_CLOSE("afs.auto.close"),
    /**
     *  售后退款回调
     */
     AFS_REFUND_CALLBACK("afs.refund.callback");

    private final String routingKey;
    public static final String EXCHANGE = "afs.direct";

    @Override
    public String exchange() {
        return EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }
}
