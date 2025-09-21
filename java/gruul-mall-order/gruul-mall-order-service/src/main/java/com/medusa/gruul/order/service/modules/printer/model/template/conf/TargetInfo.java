package com.medusa.gruul.order.service.modules.printer.model.template.conf;

import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.feie.fxml.Fxml;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TargetInfo implements Fxml {
    private TargetName name;
    private TargetMobile mobile;
    private TargetAddress address;

    @Override
    public String fxml(FeieTicketSize size) {
        String fxml = PrintFormat.fxml(size, name, mobile, address);
        if (fxml.isEmpty()) {
            return fxml;
        }
        fxml += size.getDivide();
        return fxml;
    }
}