package com.medusa.gruul.addon.freight.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.shop.api.enums.SelfShopEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 打印机信息
 *
 * @author xiaoq
 * @Description LogisticsPrint.java
 * @date 2022-08-11 15:49
 */
@Data
@TableName("t_logistics_print")
@EqualsAndHashCode(callSuper = true)
public class LogisticsPrint extends BaseEntity {

    private static final long serialVersionUID = -3313170020496239496L;

    /**
     * 打印机名称
     */
    private String printName;

    /**
     * 打印机身号
     */
    private String deviceNo;

    /**
     * 是否 自营商家
     */
    private SelfShopEnum defSelfShop;

    /**
     * 是否 自营供应商
     */
    private SelfShopEnum defSelfSupplier;
}
