package com.medusa.gruul.addon.store.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 店铺门店操作DTO
 *
 * @author xiaoq
 * @Description 店铺门店操作DTO
 * @date 2023-03-09 09:23
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopStoreOperateDTO {

    /**
     * 店铺门店ids
     */
   private Set<Long> ids;


    /**
     * 所属店铺id
     */
    private Long shopId;
}
