package com.medusa.gruul.addon.ic.modules.ic.model.bo;

import com.medusa.gruul.order.api.enums.ErrorHandlerStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2024/8/29
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ICErrorHandler {

    /**
     * 处理状态
     */
    private ErrorHandlerStatus status;

    /**
     * 异常处理时间
     */
    private LocalDateTime time;

    /**
     * 处理说明
     */
    private String desc;

}
