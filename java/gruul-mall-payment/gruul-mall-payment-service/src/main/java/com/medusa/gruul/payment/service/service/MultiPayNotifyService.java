package com.medusa.gruul.payment.service.service;


import com.egzosn.pay.common.bean.NoticeRequest;

/**
 * @author xiaoq
 * @ Description
 * @date 2022-08-01 19:03
 */
public interface MultiPayNotifyService {
    /**
     * 支付回调
     *
     * @param detailsId               商户支付配置id
     * @param httpRequestNoticeParams params
     * @return x
     */
    String payNotify(String detailsId, NoticeRequest httpRequestNoticeParams);
}
