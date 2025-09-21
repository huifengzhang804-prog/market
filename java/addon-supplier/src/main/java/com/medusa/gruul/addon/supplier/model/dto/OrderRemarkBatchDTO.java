package com.medusa.gruul.addon.supplier.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/7/29
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderRemarkBatchDTO implements Serializable {

    /**
     * 订单号
     */
    @NotNull
    @Size(min = 1)
    private Set<String> orderNos;

    /**
     * 供应商 id
     */
    @Null
    private Long supplierId;

    /**
     * 备注详情
     */
    @NotBlank
    @Size(max = 500)
    private String remark;
}
