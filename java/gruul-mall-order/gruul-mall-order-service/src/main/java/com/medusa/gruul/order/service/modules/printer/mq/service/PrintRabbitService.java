package com.medusa.gruul.order.service.modules.printer.mq.service;

import com.medusa.gruul.order.service.modules.printer.model.bo.PrintPublishBO;

/**
 * @author 张治保
 * @since 2024/11/19
 */
public interface PrintRabbitService {

    /**
     * 发送 打印消息
     *
     * @param param 打印消息数据
     */
    void print(PrintPublishBO param);
}
