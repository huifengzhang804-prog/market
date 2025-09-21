package com.medusa.gruul.cart.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/7/26
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserCartProductDTO {

    private Long userId;

    private List<ShopIdSkuIdsDTO> shopIdSkuIds;

}
