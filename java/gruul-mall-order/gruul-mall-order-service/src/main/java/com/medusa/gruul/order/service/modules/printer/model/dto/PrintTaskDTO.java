package com.medusa.gruul.order.service.modules.printer.model.dto;

import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintScene;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/22
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PrintTaskDTO {
    /**
     * 任务 id 为空是新增 不为空是编辑
     */
    private Long id;

    /**
     * 关联的业务模式
     */
    @NotNull
    private PrintMode mode;

    /**
     * 任务名称
     */
    @NotBlank
    @Size(max = 20)
    private String name;

    /**
     * 关联的打印机id
     */
    @NotNull
    private Long printerId;

    /**
     * 关联的打印机模板 id -1 表示使用原来的模板
     */
    @NotNull
    private Long templateId;

    /**
     * 打印场景
     */
    @NotNull
    private PrintScene scene;

    /**
     * 打印联数 （次数）
     */
    @NotNull
    @Min(1)
    @Max(4)
    private Integer times;

}
