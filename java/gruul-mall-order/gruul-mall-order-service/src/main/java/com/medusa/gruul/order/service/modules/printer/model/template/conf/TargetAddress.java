package com.medusa.gruul.order.service.modules.printer.model.template.conf;

import com.medusa.gruul.order.service.modules.printer.model.template.PrintPlaceHolder;

public class TargetAddress extends BindFxml<TargetAddress> {

    public TargetAddress() {
        super(true);
    }

    @Override
    protected PrintPlaceHolder placeholder() {
        return PrintPlaceHolder.targetAddress;
    }
}