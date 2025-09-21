package com.medusa.gruul.order.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.geometry.GeometryTypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.helper.Address;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.List;

/**
 * 订单收货人信息
 *
 * @author 张治保
 * @since 2022-07-19
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
@TableName(value = "t_order_receiver", autoResultMap = true)
public class OrderReceiver extends BaseEntity implements Address {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 店铺店铺订单号
     */
    private String shopOrderNo;

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
     * 用户地址定位 门店自提时为空
     */
    @TableField(typeHandler = GeometryTypeHandler.class)
    private Point location;

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
