package com.medusa.gruul.order.service.modules.printer.model.vo;

import com.medusa.gruul.order.api.enums.PrintLink;
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
public class PrintTemplateRecordVO {

    /**
     * 模板 id
     */
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 打印机品牌
     */
    private PrinterBrand brand;

    /**
     * 模板打印类型 某联
     */
    private PrintLink link;

    /**
     * 打印机纸张尺寸
     */
    private FeieTicketSize size;

    /**
     * 添加时间
     */
    private LocalDate createDate;


}
