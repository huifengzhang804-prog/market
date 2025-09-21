package com.medusa.gruul.payment.service.service.impl;

import com.egzosn.pay.common.bean.NoticeRequest;
import com.egzosn.pay.spring.boot.core.PayServiceManager;
import com.medusa.gruul.payment.service.service.MultiPayNotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author xiaoq
 * @ Description
 * @date 2022-08-01 19:04
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MultiPayNotifyServiceImpl implements MultiPayNotifyService {

    private final PayServiceManager payServiceManager;

    /**
     * 支付回调接口
     *
     * @param detailsId 商户支付配置id
     * @param request   回调请求
     * @return 回调结果 true / false
     */
    @Override
    public String payNotify(String detailsId, NoticeRequest request) {
        return payServiceManager.payBack(detailsId, request);
    }
}
