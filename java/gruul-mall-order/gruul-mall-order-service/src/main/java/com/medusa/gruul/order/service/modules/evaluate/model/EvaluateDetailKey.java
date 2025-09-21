package com.medusa.gruul.order.service.modules.evaluate.model;

import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/12/30
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class EvaluateDetailKey extends ShopProductSkuKey {

    /**
     * sku 规格
     */
    private List<String> specs;
}
