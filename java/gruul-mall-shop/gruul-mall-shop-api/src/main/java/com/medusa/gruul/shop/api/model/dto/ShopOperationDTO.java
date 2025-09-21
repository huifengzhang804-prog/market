package com.medusa.gruul.shop.api.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 店铺操作dto
 *
 * @author xiaoq
 * @Description ShopOperationDTO.java
 * @date 2023-12-08 15:03
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopOperationDTO {

    /**
     * 用户id
     */
    private  Long userId;

    /**
     * 店铺ids
     */
    private Set<Long> shopIds;
}
