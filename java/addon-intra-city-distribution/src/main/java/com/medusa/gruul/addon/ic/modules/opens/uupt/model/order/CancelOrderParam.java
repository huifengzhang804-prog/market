package com.medusa.gruul.addon.ic.modules.opens.uupt.model.order;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@Accessors(chain = true)
public class CancelOrderParam extends OrderKey {

    /**
     * 是 取消原因
     */
    private String reason;
}
