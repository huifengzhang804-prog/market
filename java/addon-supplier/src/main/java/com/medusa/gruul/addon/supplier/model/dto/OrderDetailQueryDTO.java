package com.medusa.gruul.addon.supplier.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2023/7/24
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderDetailQueryDTO extends OrderMatchQueryDTO {
    /**
     * 是否需要查询供应商信息
     */
    private Boolean needSupplier;

}
