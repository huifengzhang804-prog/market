package com.medusa.gruul.addon.supplier.model.dto;

import com.medusa.gruul.addon.supplier.model.bo.Receiver;
import com.medusa.gruul.common.model.enums.SellType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/7/19
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderCreateDTO implements Serializable {

    /**
     * 备注
     */

    private String remark;

    /**
     * 商品类型
     */
    @NotNull
    private SellType sellType = SellType.PURCHASE;

    /**
     * 收货人信息
     */
    @NotNull
    @Valid
    private Receiver receiver;

    /**
     * 供应商id与商品id与sku id映射
     */
    @NotNull
    @Size(min = 1, max = 15)
    @Valid
    private Set<OrderSupplierDTO> suppliers;
}
