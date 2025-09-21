package com.medusa.gruul.addon.supplier.model.bo;

import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/7/27
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "key")
@Accessors(chain = true)
public class StorageSkuRecord implements Serializable {

    /**
     * sku key
     */
    private ShopProductSkuKey key;

    /**
     * 入库数量
     */
    private Integer num;
}
