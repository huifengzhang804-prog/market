package com.medusa.gruul.addon.integral.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderDeliverType;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderSourceType;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderStatus;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import com.medusa.gruul.order.api.entity.OrderTimeout;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 积分订单
 *
 * @author xiaoq
 * @Description 积分订单
 * @date 2023-01-31 13:31
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_integral_order", autoResultMap = true)
public class IntegralOrder extends BaseEntity {

    /**
     * 积分订单来源
     */
    private IntegralOrderSourceType source;


    /**
     * 订单号
     */
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
     * 买家备注
     */
    private String buyerRemark;

    /**
     * 卖家备注
     */
    private String sellerRemark;
    /**
     * 订单状态
     */
    private IntegralOrderStatus status;

    /**
     * 积分商品id
     */
    private Long productId;


    /**
     * 积分订单的发货方式
     */
    private IntegralOrderDeliverType integralOrderDeliverType;


    /**
     * 物流公司编号
     */
    private String expressCompanyName;

    /**
     * 物流公司名称
     */
    private String expressName;


    /**
     * 包裹运单号
     */
    private String expressNo;

    /**
     * 积分商品名称
     */
    private String productName;

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * 积分价格
     */
    private Long price;

    /**
     * 混合支付金额
     */
    private Long salePrice;

    /**
     * 积分商品主图
     */
    private String image;


    /**
     * 运费金额
     */
    private Long freightPrice;


    /**
     * 付款时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 订单成功时间
     */
    private LocalDateTime accomplishTime;

    /**
     * 归属平台
     */
    private Platform affiliationPlatform;

    /**
     * 商品类型
     */
    private ProductType productType;

    /**
     * 关键节点超时时间 key_node_timeout
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private OrderTimeout timeout;

    /**
     * 收货地址
     */
    @TableField(exist = false)
    private IntegralOrderReceiver integralOrderReceiver;
    /**
     * 支付信息
     */
    @TableField(exist = false)
    private IntegralOrderPayment integralOrderPayment;


}
