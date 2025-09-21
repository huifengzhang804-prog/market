package com.medusa.gruul.shop.service.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.shop.api.entity.ShopLogisticsAddress;
import com.medusa.gruul.shop.api.enums.AddressDefaultEnum;
import com.medusa.gruul.shop.api.enums.SelfShopEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;


/**
 * @Description: 物流地址DTO
 * @Author: xiaoq
 * @Date : 2022-05-05 10:25
 */
@Data
public class ShopLogisticsAddressDTO {

    private Long id;

    /**
     * 联系人名称
     */
    @NotNull
    private String contactName;

    /**
     * 联系人电话
     */
    @NotNull
    private String contactPhone;

    /**
     * 省市区
     */
    @NotNull
    private List<String> area;

    /**
     * 详细地址
     */
    @NotNull
    private String address;

    /**
     * 邮编码
     */
    private String zipCode;

    /**
     * 是否为默认发货地址 0默认 1未默认
     */
    private AddressDefaultEnum defSend;

    /**
     * 是否为默认收货地址 0默认 1未默认
     */
    private AddressDefaultEnum defReceive;

    /**
     * 是否 自营商家
     */
    private SelfShopEnum defSelfShop;

    /**
     * 是否 自营供应商
     */
    private SelfShopEnum defSelfSupplier;

    public ShopLogisticsAddress coverLogisticsAddress() {
        ShopLogisticsAddress shopLogisticsAddress = new ShopLogisticsAddress();
        BeanUtil.copyProperties(this, shopLogisticsAddress);
        return shopLogisticsAddress;
    }
}
