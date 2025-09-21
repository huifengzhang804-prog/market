package com.medusa.gruul.order.api.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.order.api.addon.activity.ActivityResp;
import com.medusa.gruul.order.api.enums.ICShopOrderStatus;
import com.medusa.gruul.order.api.enums.OrderSourceType;
import com.medusa.gruul.order.api.enums.OrderStatus;
import com.medusa.gruul.order.api.enums.PrintLink;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.List;
import java.util.Set;

/**
 * @author 张治保
 * @since 2022-06-08
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_order", autoResultMap = true)
@ToString
@NoArgsConstructor
public class Order extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -2354235235266777221L;

    /**
     * 订单配送类型
     */
    private DistributionMode distributionMode;

    /**
     * 订单来源
     */
    private OrderSourceType source;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 订单号
     */
    @TableField("`no`")
    private String no;

    /**
     * 买家用户id
     */
    private Long buyerId;

    /**
     * 买家昵称
     */
    private String buyerNickname;

    /**
     * 买家头像
     */
    private String buyerAvatar;


    /**
     * 订单状态
     */
    @TableField("`status`")
    private OrderStatus status;

    /**
     * 订单类型
     */
    private OrderType type;

    /**
     * 关键节点超时时间 key_node_timeout
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private OrderTimeout timeout;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 是否拥有优先权(发货)
     */
    private Boolean isPriority;

    /**
     * 订单平台
     */
    private Platform platform;

    /**
     * 附加数据
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private JSONObject extra;


    /* 以下为业务处理数据 */

    @TableField(exist = false)
    private OrderPayment orderPayment;

    @TableField(exist = false)
    private List<OrderDiscount> orderDiscounts;

    @TableField(exist = false)
    private List<ShopOrder> shopOrders;

    @TableField(exist = false)
    private OrderReceiver orderReceiver;

    @TableField(exist = false)
    private List<ShopOrderPackage> shopOrderPackages;

    /**
     * 业务处理字段
     */
    @TableField(exist = false)
    private ActivityResp activityResp;

    /**
     * 同城配送单状态 同城配送时生效
     */
    @TableField(exist = false)
    private ICShopOrderStatus icStatus;

    /**
     * 同城配送订单状态描述（用于异常描述）
     */
    @TableField(exist = false)
    private String icStatusDesc;


    /**
     * 同城配送｜门店配送 查询的打印联信息
     */
    @TableField(exist = false)
    private Set<PrintLink> printLinks;

    public static Order copyOf(Order order) {
        Order newOrder = new Order();
        newOrder
                .setSource(order.getSource())
                .setPlatform(order.getPlatform())
                .setBuyerId(order.getBuyerId())
                .setBuyerNickname(order.getBuyerNickname())
                .setBuyerAvatar(order.getBuyerAvatar())
                .setActivityId(order.getActivityId())
                .setNo(order.getNo())
                .setStatus(order.getStatus())
                .setType(order.getType())
                .setTimeout(order.getTimeout())
                .setRemark(order.getRemark())
                .setIsPriority(order.getIsPriority())
                .setExtra(order.getExtra())
                .setOrderPayment(order.getOrderPayment())
                .setOrderDiscounts(order.getOrderDiscounts())
                .setShopOrders(order.getShopOrders())
                .setOrderReceiver(order.getOrderReceiver())
                .setShopOrderPackages(order.getShopOrderPackages())
                .setActivityResp(order.getActivityResp())
                .setId(order.getId())
                .setVersion(order.getVersion())
                .setDeleted(order.getDeleted())
                .setCreateTime(order.getCreateTime())
                .setUpdateTime(order.getUpdateTime());
        return newOrder;
    }


    /**
     * 获取订单支付后的状态
     *
     * @return OrderStatus
     */
    public OrderStatus paidStatus() {
        return OrderType.TEAM == type ? OrderStatus.TEAMING : OrderStatus.PAID;
    }
}
