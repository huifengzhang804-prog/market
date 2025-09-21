package com.medusa.gruul.storage.api.bo;

import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * sku Key 与 库存销量映射
 *
 * @author 张治保
 * date 2023/3/8
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "key")
@ToString
public class SkuKeyStSvBO implements Serializable {

    /**
     * sku key
     */
    private ActivityShopProductSkuKey key;

    /**
     * 库存和销量
     */
    private StSvBo stSv;

}
