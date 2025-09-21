package com.medusa.gruul.addon.store.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.store.model.enums.StoreStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xiaoq
 * @Description 店铺门店查询param
 * @date 2023-03-08 14:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopStoreParam extends Page<Object> {

    /**
     * 门店状态
     */
    private StoreStatus status;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 门店名称
     */
    private String storeName;


    /**
     * 店铺名称
     */
    private String shopName;

}
