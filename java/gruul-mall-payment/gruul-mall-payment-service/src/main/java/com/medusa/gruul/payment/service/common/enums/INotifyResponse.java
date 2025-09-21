package com.medusa.gruul.payment.service.common.enums;

/**
 * @author 张治保
 * date 2021/12/23
 */
public interface INotifyResponse {
    /**
     * 状态码
     * @return code
     */
    String getCode();

    /**
     * 业务信息
     *
     * @return msg
     */
    String getMsg();
}
