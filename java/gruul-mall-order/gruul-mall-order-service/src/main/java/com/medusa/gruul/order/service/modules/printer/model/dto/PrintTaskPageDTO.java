package com.medusa.gruul.order.service.modules.printer.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.order.service.modules.printer.model.enums.PrintMode;
import com.medusa.gruul.order.service.modules.printer.mp.entity.PrintTask;
import jakarta.validation.constraints.NotNull;
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
@Accessors(chain = true)
@ToString(callSuper = true)
public class PrintTaskPageDTO extends Page<PrintTask> {
    /**
     * 关联的业务模式
     */
    @NotNull
    private PrintMode mode;
}
