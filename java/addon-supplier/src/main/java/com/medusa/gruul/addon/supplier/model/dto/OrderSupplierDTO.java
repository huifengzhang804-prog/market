package com.medusa.gruul.addon.supplier.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/7/19
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = "productSkus")
@Accessors(chain = true)
public class OrderSupplierDTO implements Serializable {

    /**
     * 供应商 id
     */
    @NotNull
    private Long supplierId;

    /**
     * 商品id与sku id映射
     */
    @NotNull
    @Size(min = 1, max = 15)
    @Valid
    private Map<Long, Set<OrderSupplierSkuDTO>> productSkus;


}
