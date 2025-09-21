package com.medusa.gruul.order.service.modules.printer.model.vo;

import com.medusa.gruul.order.api.enums.PrintLink;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintScene;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author 张治保
 * @since 2024/8/22
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PrintTaskRecordVO {

    /**
     * 任务 id
     */
    private Long id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 绑定的打印机 id
     */
    private Long printerId;

    /**
     * 绑定的打印机名称
     */
    private String printerName;

    /**
     * 打印类型  某联
     */
    private PrintLink link;

    /**
     * 绑定的打印模板名称
     */
    private String templateName;

    /**
     * 打印场景
     */
    private PrintScene scene;

    /**
     * 打印联数 （次数）
     */
    private Integer times;

    /**
     * 打印纸宽
     */
    private FeieTicketSize size;

    /**
     * 添加时间
     */
    private LocalDate createDate;

}
