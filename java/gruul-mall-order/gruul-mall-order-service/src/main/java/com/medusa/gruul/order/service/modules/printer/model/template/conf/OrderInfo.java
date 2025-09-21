package com.medusa.gruul.order.service.modules.printer.model.template.conf;

import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.feie.fxml.Fxml;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author 张治保
 * @since 2024/8/20
 */
@Getter
@Setter
@Accessors(chain = true)
public class OrderInfo implements Fxml {
    /**
     * 订单号  支持样式
     */
    private OrderNo orderNo;

    /**
     * 支付方式 支持样式
     */
    private PayType payType;

    /**
     * 下单时间 支持样式
     */
    private OrderTime orderTime;

    /**
     * 支付时间 支持样式
     */
    private PayTime payTime;

    @Override
    public String fxml(FeieTicketSize size) {
        String fxml = PrintFormat.fxml(size, orderNo, payType, orderTime, payTime);
        if (fxml.isEmpty()) {
            return fxml;
        }
        fxml += size.getDivide();
        return fxml;
    }
}