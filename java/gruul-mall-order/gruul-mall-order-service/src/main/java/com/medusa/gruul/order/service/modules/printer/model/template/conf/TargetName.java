package com.medusa.gruul.order.service.modules.printer.model.template.conf;

import com.medusa.gruul.order.service.modules.printer.model.template.PrintPlaceHolder;

public class TargetName extends BindFxml<TargetName> {

    public TargetName() {
        super(true);
    }

    @Override
    protected PrintPlaceHolder placeholder() {
        return PrintPlaceHolder.targetName;
    }
}