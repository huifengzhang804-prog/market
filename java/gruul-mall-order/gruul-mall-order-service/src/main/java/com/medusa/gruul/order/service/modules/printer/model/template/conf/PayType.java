package com.medusa.gruul.order.service.modules.printer.model.template.conf;

import com.medusa.gruul.order.service.modules.printer.model.template.PrintPlaceHolder;

public class PayType extends BindFxml<PayType> {

    public PayType() {
        super(true);
    }

    @Override
    protected PrintPlaceHolder placeholder() {
        return PrintPlaceHolder.payType;
    }

}