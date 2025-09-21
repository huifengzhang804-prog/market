package com.medusa.gruul.addon.supplier.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/7/25
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderPayAuditDTO implements Serializable {

    /**
     * 订单号
     */
    @NotBlank
    private String orderNo;

    /**
     * 审核结果
     */
    @NotNull
    private Boolean success;
}
