package com.medusa.gruul.order.service.modules.printer.model.template.conf;

import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintPlaceHolder;


/**
 * @author 张治保
 * @since 2024/8/21
 */
public class OrderRemark extends BindFxml<OrderRemark> {
    public OrderRemark() {
        super(true);
    }

    @Override
    protected PrintPlaceHolder placeholder() {
        return PrintPlaceHolder.orderRemark;
    }

    @Override
    public String fxml(FeieTicketSize size) {
        String fxml = super.fxml(size);
        if (fxml.isEmpty()) {
            return fxml;
        }
        return size.getDivide() + fxml;
    }
}