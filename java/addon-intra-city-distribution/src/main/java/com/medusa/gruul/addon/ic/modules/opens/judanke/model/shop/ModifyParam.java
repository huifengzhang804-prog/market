package com.medusa.gruul.addon.ic.modules.opens.judanke.model.shop;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@Accessors(chain = true)
public class ModifyParam extends CreateParam {
    /**
     * shop_id	int	是 店铺id
     */
    private Long shopId;
}
