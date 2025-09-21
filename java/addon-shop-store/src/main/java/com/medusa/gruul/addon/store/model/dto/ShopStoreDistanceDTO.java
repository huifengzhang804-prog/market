package com.medusa.gruul.addon.store.model.dto;

import com.vividsolutions.jts.geom.Point;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 店铺门店距离DTO
 *
 * @author xiaoq
 * @Description 店铺门店距离DTO
 * @date 2023-03-14 11:16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopStoreDistanceDTO {
    /**
     * 用户定位选点
     */
//    @NotNull
    private Point point;

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

}
