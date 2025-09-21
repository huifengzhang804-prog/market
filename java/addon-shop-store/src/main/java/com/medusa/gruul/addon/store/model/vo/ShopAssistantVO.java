package com.medusa.gruul.addon.store.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 店铺店员VO
 *
 * @author xiaoq
 * @Description 店铺店员VO
 * @date 2023-03-22 11:13
 */
@Data
@Accessors(chain = true)
public class ShopAssistantVO {

    private Long id;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 门店id
     */
    private Long storeId;


    /**
     * 店员手机号
     */
    private String assistantPhone;

    /**
     * 门店name
     */
    private String storeName;
}
