package com.medusa.gruul.order.service.modules.printer.model.template.conf;

import com.medusa.gruul.order.service.modules.printer.model.template.PrintPlaceHolder;

public class PayPrice extends BindFxml<PayPrice> {
    public PayPrice() {
        super(true);
    }

    @Override
    protected PrintPlaceHolder placeholder() {
        return null;
    }
}