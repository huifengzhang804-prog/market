package com.medusa.gruul.order.service.modules.printer.model.template.conf;

import com.medusa.gruul.order.service.modules.printer.model.template.PrintPlaceHolder;

/**
 * @author 张治保
 * @since 2024/8/20
 */
public class OrderNo extends BindFxml<OrderNo> {

    public OrderNo() {
        super(true);
    }

    @Override
    protected PrintPlaceHolder placeholder() {
        return PrintPlaceHolder.orderNo;
    }

}
