package com.medusa.gruul.shop.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.shop.api.entity.ShopLogisticsAddress;
import com.medusa.gruul.shop.api.enums.AddressTypeEnum;
import com.medusa.gruul.shop.service.model.dto.ShopLogisticsAddressDTO;
import com.medusa.gruul.shop.service.model.param.ShopLogisticsAddressParam;

/**
 * @author xiaoq
 * @Description 店铺物流地址管理service
 * @date 2023-05-08 13:56
 */
public interface ShopLogisticsAddressManageService {

    /**
     * 新增或修改物流地址
     *
     * @param logisticsAddress 物流地址
     */
    void editAddress(ShopLogisticsAddressDTO logisticsAddress);


    /**
     * 删除物流地址
     *
     * @param id 物流地址id
     */
    void delAddress(Long id);

    /**
     * 获取物流地址信息
     *
     * @param logisticsAddressParam 分页param
     * @return IPage<物流收发货地址管理>
     */
    IPage<ShopLogisticsAddress> getAddressListByPage(ShopLogisticsAddressParam logisticsAddressParam);

    /**
     * 获取物流信息
     *
     * @param id 物流地址id
     * @return ShopLogisticsAddress
     */
    ShopLogisticsAddress getAddressById(Long id);


    /**
     * 设置收货发货默认地址
     *
     * @param id   地址id
     * @param type 收发货类型
     */
    void setDefAddress(Long id, AddressTypeEnum type);
}
