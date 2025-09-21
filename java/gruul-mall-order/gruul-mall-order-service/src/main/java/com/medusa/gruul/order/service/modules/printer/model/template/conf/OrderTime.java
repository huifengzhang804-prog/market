package com.medusa.gruul.order.service.modules.printer.model.template.conf;

import com.medusa.gruul.order.service.modules.printer.model.template.PrintPlaceHolder;

public class OrderTime extends BindFxml<OrderTime> {

    public OrderTime() {
        super(true);
    }

    @Override
    protected PrintPlaceHolder placeholder() {
        return PrintPlaceHolder.orderTime;
    }

}