package com.medusa.gruul.search.api.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * @author: wufuzhong
 * @Date: 2023/10/14 15:22:41
 * @Description：商品分销状态
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductDistributed implements Serializable {

    @NotNull
    private Long shopId;

    /**
     * es中唯一id
     */
    @NotNull
    private Set<Long> productIds;

    /**
     * 是否分销商品(默认false)
     */
    private Boolean isDistributed = Boolean.FALSE;
}
