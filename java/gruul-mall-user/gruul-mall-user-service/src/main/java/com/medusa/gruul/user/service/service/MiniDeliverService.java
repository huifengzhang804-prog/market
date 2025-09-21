package com.medusa.gruul.user.service.service;

import com.medusa.gruul.common.system.model.model.Platform;

/**
 * 小程序发货服务层
 *
 * @author xiaoq
 * @Description MiniDeliverService.java
 * @date 2024-01-10 17:57
 */
public interface MiniDeliverService {

    /**
     * 小程序发货
     *
     * @param transactionId 交易流水号id
     * @param openId        openId
     * @param desc          商品信息描述
     * @param platform      支付平台类型
     */
    void miniDeliver(String transactionId, String openId, String desc, Platform platform);
}
