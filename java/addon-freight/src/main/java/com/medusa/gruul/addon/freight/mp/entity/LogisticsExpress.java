package com.medusa.gruul.addon.freight.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.shop.api.enums.SelfShopEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 物流公司服务表
 *
 * @author xiaoq
 * @Description LogisticsExpress.java
 * @date 2022-06-14 15:49
 */

@Getter
@Setter
@TableName("t_logistics_express")
public class LogisticsExpress extends BaseEntity {

    /**
     * 物流打印机id
     */
    private Long logisticsPrintId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 物流配置表id
     */
    private Long freightId;


    /**
     * 客户号
     */
    private String customerCode;


    /**
     * 客户密码
     */
    private String customerPassword;

    /**
     * 网点名称
     */
    private String networkName;

    /**
     * 网点编号
     */
    private String networkCode;

    /**
     * 是否 自营商家
     */
    private SelfShopEnum defSelfShop;

    /**
     * 是否 自营供应商
     */
    private SelfShopEnum defSelfSupplier;


}
