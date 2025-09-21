package com.medusa.gruul.addon.freight.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 物流设置
 *
 * @author xiaoq
 * @Description LogisticsSettings.java
 * @date 2022-08-12 10:08
 */

@Data
@TableName("t_logistics_settings")
@EqualsAndHashCode(callSuper = true)
public class LogisticsSettings extends BaseEntity {

    private static final long serialVersionUID = 4143487724600812312L;
    /**
     * 快递100授权key
     */
    @TableField("`key`")
    private String key;

    /**
     * 快递100客户号
     */
    @TableField("customer")
    private String customer;

    @TableField("secret")
    private String secret;

    /**
     * 店铺id
     */
    @TableField("shop_id")
    private Long shopId;
}
