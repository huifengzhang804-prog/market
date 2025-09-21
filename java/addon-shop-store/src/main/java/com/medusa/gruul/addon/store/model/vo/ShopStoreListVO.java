package com.medusa.gruul.addon.store.model.vo;

import com.medusa.gruul.addon.store.model.enums.StoreStatus;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 店铺门店列表VO
 *
 * @author xiaoq
 * @Description 门店列表VO
 * @date 2023-03-08 14:44
 */
@Data
@Accessors(chain = true)
public class ShopStoreListVO {

    private Long id;

    private Long shopId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 店铺名称
     */
    private String shopName;


    /**
     * 负责人名称
     */
    private String functionaryName;

    /**
     * 负责人电话号
     */
    private String functionaryPhone;
    /**
     * 门店状态
     */
    private StoreStatus status;

    /**
     * 详细地址
     */
    private String detailedAddress;





}
