package com.medusa.gruul.order.service.modules.printer.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.order.api.enums.PrintLink;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.mp.entity.PrintTemplate;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 张治保
 * @since 2024/8/21
 */
@Getter
@Setter
public class PrintTemplatePageDTO extends Page<PrintTemplate> {

    /**
     * 关联的业务模式
     */
    @NotNull
    private PrintMode mode;

    /**
     * 打印机纸张尺寸
     */
    private FeieTicketSize ticketSize;

    /**
     * 打印类型 某联
     */
    private PrintLink link;


}
