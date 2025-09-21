package com.medusa.gruul.search.service.model.nested;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/12/22
 */
@Getter
@Setter
@Accessors(chain = true)
public class NestedShop implements Serializable {
    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String name;
}
