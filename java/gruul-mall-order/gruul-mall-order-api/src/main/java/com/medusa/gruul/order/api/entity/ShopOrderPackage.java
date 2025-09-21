package com.medusa.gruul.order.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.order.api.enums.DeliverType;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.enums.SelfShopTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 订单物流配送
 *
 * @author 张治保
 * @since 2022-06-08
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_shop_order_package", autoResultMap = true)
public class ShopOrderPackage extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 包裹状态
     */
    private PackageStatus status;

    /**
     * 配送方式
     */
    private DeliverType type;

    /**
     * 快递公司名称
     */
    private String expressCompanyName;

    /**
     * 快递公司代码
     */
    private String expressCompanyCode;

    /**
     * 快递单号
     */
    private String expressNo;

    /**
     * 收货人名称
     */
    private String receiverName;

    /**
     * 收货人电话
     */
    private String receiverMobile;

    /**
     * 收货人详细地址
     */
    private String receiverAddress;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 确认收货时间
     */
    private LocalDateTime confirmTime;

    /**
     * 评论时间
     */
    private LocalDateTime commentTime;

    /**
     * 发货备注
     */
    private String remark;

    /**
     * 发货方店铺、供应商id
     */
    private Long deliverShopId;

    /**
     * 自营店铺类型
     */
    private SelfShopTypeEnum selfShopType;

    /**
     * 发货方
     */
    @TableField(exist = false)
    private String deliverShopName;

    /**
     * 是否需要发送延迟确认收货mq默认为 true
     */
    @TableField(exist = false)
    private boolean sendDelayConfirm = true;

}
