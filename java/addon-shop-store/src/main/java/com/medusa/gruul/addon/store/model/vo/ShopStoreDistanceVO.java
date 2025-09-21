package com.medusa.gruul.addon.store.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 店铺门店距离VO
 *
 * @author xiaoq
 * @Description 店铺门店距离VO
 * @date 2023-03-15 20:39
 */
@Data
@Accessors(chain = true)
public class ShopStoreDistanceVO {

    private Long id;

    private Long shopId;

    /**
     * 门店名称
     */
    private String storeName;


    /**
     * 负责人电话号
     */
    private String functionaryPhone;

    /**
     * 详细地址
     */
    private String detailedAddress;

    /**
     * 距用户选点VO
     */
    private Double distance;
}
