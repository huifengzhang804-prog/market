package com.medusa.gruul.addon.ic.modules.ic.model.dto;


import com.medusa.gruul.order.api.enums.ErrorHandlerStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/9/10
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ICErrorHandleDTO {

    /**
     * 订单号
     */
    @NotBlank
    private String orderNo;

    /**
     * 重置状态
     */
    @NotNull
    private ErrorHandlerStatus status;

    /**
     * 处理说明
     */
    @NotBlank
    @Size(max = 30)
    private String desc;
}
