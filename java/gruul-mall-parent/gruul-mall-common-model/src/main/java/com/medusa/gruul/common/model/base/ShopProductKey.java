package com.medusa.gruul.common.model.base;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/11/16
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShopProductKey implements Serializable {

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 商品id
     */
    @NotNull
    private Long productId;
}
