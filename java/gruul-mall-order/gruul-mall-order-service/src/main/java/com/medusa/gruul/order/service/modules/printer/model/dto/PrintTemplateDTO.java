package com.medusa.gruul.order.service.modules.printer.model.dto;

import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.order.api.enums.PrintLink;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeieTicketSize;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrinterBrand;
import com.medusa.gruul.order.service.modules.printer.model.template.PrintTemplateConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PrintTemplateDTO implements BaseDTO {

    /**
     * 打印机模板 id 为空是新增 不为空是编辑
     */
    private Long id;

    /**
     * 关联的业务模式
     */
    @NotNull
    private PrintMode mode;

    /**
     * 模板名称
     */
    @NotBlank
    @Size(max = 20)
    private String name;

    /**
     * 打印机品牌
     */
    @NotNull
    private PrinterBrand brand;

    /**
     * 打印类型  某联
     */
    @NotNull
    private PrintLink link;

    /**
     * 打印纸宽
     */
    @NotNull
    private FeieTicketSize size;

    /**
     * 原始模板配置
     */
    @NotNull
    @Valid
    private PrintTemplateConfig config;

}
