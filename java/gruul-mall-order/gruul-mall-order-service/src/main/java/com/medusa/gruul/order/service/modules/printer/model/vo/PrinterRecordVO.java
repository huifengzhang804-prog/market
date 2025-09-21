package com.medusa.gruul.order.service.modules.printer.model.vo;

import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeiePrinterStatus;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeiePrinterType;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrinterBrand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author 张治保
 * @since 2024/8/21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PrinterRecordVO {

    /**
     * 打印机 id
     */
    private Long id;

    /**
     * 打印机名称
     */
    private String name;

    /**
     * 打印机品牌
     */
    private PrinterBrand brand;

    /**
     * 纸张尺寸
     */
    private FeieTicketSize size;

    /**
     * 机型
     */
    private FeiePrinterType type;

    /**
     * status状态，0：离线 1：在线，工作状态正常 2：在线，工作状态不正常
     */
    private FeiePrinterStatus status;

    /**
     * 机器位置
     */
    private String place;

    /**
     * 添加时间
     */
    private LocalDate createDate;

    /**
     * 是否已被打印任务绑定
     */
    private Boolean bound;

}
