package com.medusa.gruul.addon.supplier.model.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/7/19
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = "num")
@Accessors(chain = true)
public class OrderSupplierSkuDTO implements Serializable {

    /**
     * 供应商 id
     */
    @NotNull
    private Long skuId;


    /**
     * 购买数量
     */
    @NotNull
    @Min(1)
    private Integer num;


    /**
     * 当前商品的sku key
     */
    @JSONField(serialize = false, deserialize = false)
    private ActivityShopProductSkuKey skuKey;

}
