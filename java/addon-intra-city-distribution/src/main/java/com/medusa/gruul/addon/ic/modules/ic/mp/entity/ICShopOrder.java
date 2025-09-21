package com.medusa.gruul.addon.ic.modules.ic.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICCourier;
import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICErrorHandler;
import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICOpen;
import com.medusa.gruul.addon.ic.modules.ic.model.bo.ICOrderTimes;
import com.medusa.gruul.addon.ic.modules.ic.model.enums.ICDeliveryType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.order.api.enums.ICShopOrderStatus;
import com.medusa.gruul.order.api.model.ic.ICProductSku;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 同城店铺配送订单
 *
 * @author 张治保
 * @since 2024/8/27
 */
@Getter
@Setter
@ToString
@TableName(value = "t_ic_shop_order", autoResultMap = true)
@Accessors(chain = true)
public class ICShopOrder extends BaseEntity {
    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 订单类型
     */
    private ICDeliveryType type;

    /**
     * 配送单号 （用于开放平台的三方订单号（存在不支持同一订单多次发单问题））
     */
    private String icNo;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 取件码
     */
    private String pickupCode;

    /**
     * 配送订单状态
     */
    @TableField(value = "`status`")
    private ICShopOrderStatus status;

    /**
     * 各时间节点
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private ICOrderTimes times;

    /**
     * 接单的店员用户id（商家自配送时生效）
     */
    private Long clerkUserId;

    /**
     * 收货地址
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private UserAddressDTO receiver;

    /**
     * 商品列表 第三方平台配送时为空
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<ICProductSku> skus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 配送员信息
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private ICCourier courier;

    /**
     * 异常处理
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private ICErrorHandler errorHandler;

    /**
     * 开放平台相关数据 商家自营时为空
     */
    @TableField(value = "`open`", typeHandler = Fastjson2TypeHandler.class)
    private ICOpen open;

    /**
     * 配送时长 单位秒
     */
    @TableField(exist = false)
    private Long deliverSeconds;

    /**
     * 异常是否已处理
     */
    @TableField(exist = false)
    private Boolean errorHandled;

    /**
     * 异常是否已处理
     */
    @TableField(exist = false)
    private String statusDesc;


}
