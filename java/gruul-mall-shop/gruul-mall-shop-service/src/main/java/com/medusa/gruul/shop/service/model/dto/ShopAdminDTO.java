package com.medusa.gruul.shop.service.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author 张治保
 * date 2022/4/19
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopAdminDTO {

    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 用户id
     */
    private Long userId;
}
