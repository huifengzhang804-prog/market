package com.medusa.gruul.order.service.modules.printer.feie.fxml;

import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;

/**
 * 渲染飞鹅打印内容的顶层抽象类
 *
 * @author 张治保
 */
public interface Fxml {

    /**
     * 渲染飞鹅打印内容fxml 格式
     *
     * @param size 票纸大小
     * @return fxml 格式的内容
     */
    String fxml(FeieTicketSize size);
}