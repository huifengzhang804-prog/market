package com.medusa.gruul.order.service.modules.printer.feie.api.model;

import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeiePrinterStatus;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeiePrinterType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * @since 2024/8/14
 */
@Getter
@Setter
@ToString
public class PrinterInfoResp {
    /**
     * model机型，0：58小票机，1：80小票机，2：标签机，3：出餐宝
     */
    private FeiePrinterType model;

    /**
     * status状态，0：离线 1：在线，工作状态正常 2：在线，工作状态不正常
     */
    private FeiePrinterStatus status;
}
