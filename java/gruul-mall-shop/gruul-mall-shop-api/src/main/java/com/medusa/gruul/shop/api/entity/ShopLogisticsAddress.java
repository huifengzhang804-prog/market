package com.medusa.gruul.shop.api.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.shop.api.enums.AddressDefaultEnum;
import com.medusa.gruul.shop.api.enums.SelfShopEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * @Description: 物流收发货地址管理
 * @Author: xiaoq
 * @Date : 2022-05-01 12:03
 */
@Getter
@Setter
@TableName(value = "t_shop_logistics_address", autoResultMap = true)
public class ShopLogisticsAddress extends BaseEntity {


    private Long shopId;
    /**
     * 联系人名称
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 邮编码
     */
    private String zipCode;

    /**
     * 省市区
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> area;

    /**
     * 详细地址
     */
    private String address;

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

}
