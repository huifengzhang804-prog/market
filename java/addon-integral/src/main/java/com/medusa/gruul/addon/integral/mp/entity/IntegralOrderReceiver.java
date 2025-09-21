package com.medusa.gruul.addon.integral.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.helper.Address;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 积分订单收货表
 *
 * @author shishuqian
 * date 2023/2/1
 * time 22:22
 **/
@Getter
@Setter
@Accessors(chain = true)
@ToString
@TableName(value = "t_integral_order_receiver", autoResultMap = true)
public class IntegralOrderReceiver extends BaseEntity implements Address {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;


    /**
     * 收货人名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 收货人电话
     */
    private String mobile;

    /**
     * 省市区
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> area;

    /**
     * 收货人详细地址
     */
    private String address;


}