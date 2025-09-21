package com.medusa.gruul.addon.store.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 门店订单备货DTO
 *
 * @author xiaoq
 * @Description 门店订单备货DTO
 * @date 2023-03-16 18:52
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class StoreOrderStockUpDTO {

    /**
     * 订单号s
     */
    @Size(min = 1)
    private Set<String> orderNos;

    /**
     * 店铺id
     */
    private Long shopId;


    /**
     * 店铺门店id
     */
    private Long shopStoreId;

}
