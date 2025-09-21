package com.medusa.gruul.order.service.modules.printer.model.dto;

import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/11/7
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PrinterEditDTO {

    /**
     * 打印机 主键id
     */
    @NotNull
    private Long id;

    /**
     * 关联的业务模式
     */
    @NotNull
    private PrintMode mode;

    /**
     * 打印机名称
     */
    @NotBlank
    @Size(max = 20)
    private String name;

    /**
     * 打印机使用位置
     */
    private String place;
}
