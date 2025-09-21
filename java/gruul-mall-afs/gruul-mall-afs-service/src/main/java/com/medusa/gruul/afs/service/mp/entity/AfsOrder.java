package com.medusa.gruul.afs.service.mp.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.afs.api.enums.AfsReason;
import com.medusa.gruul.afs.api.enums.AfsType;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.order.api.enums.PackageStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 售后工单表
 * </p>
 *
 * @author 张治保
 * @since 2022-08-03
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_afs_order", autoResultMap = true)
public class AfsOrder extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 售后工单号
     */
    @TableField("`no`")
    private String no;

    /**
     * 售后工单类型
     */
    private AfsType type;

    /**
     * 工单状态
     */
    @TableField("`status`")
    private AfsStatus status;
    /**
     * 售后工单状态描述
     */
    @TableField(exist = false)
    private String statusContent;

    /**
     * 包裹id
     */
    private Long packageId;

    /**
     * 当前包裹状态
     */
    private PackageStatus packageStatus;
    /**
     * 选择售后的原因
     */
    private AfsReason reason;

    /**
     * 买家id
     */
    private Long buyerId;

    /**
     * 买家昵称
     */
    private String buyerNickname;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 供应商 id
     */
    private Long supplierId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺logo
     */
    private String shopLogo;

    /**
     * 店铺订单商品id
     */
    private Long shopOrderItemId;

    /**
     * 申请退款金额
     */
    private Long refundAmount;

    /**
     * 退款说明
     */
    @TableField("`explain`")
    private String explain;


    /**
     * 售后工单备注
     */
    private String remark;

    /**
     * 店铺类型
     */
    private ShopType shopType;
    /**
     * 是否急速售后
     */
    @TableField("`quickness_afs`")
    private boolean quicknessAfs;


    /**
     * t_order 附加数据
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private JSONObject extra;
    /**
     * 关键节点超时时间
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private AfsKeyNodeTimeout keyNodeTimeout;


    @TableField(exist = false)
    private AfsOrderItem afsOrderItem;

    @TableField(exist = false)
    private AfsPackage afsPackage;

    @TableField(exist = false)
    private AfsOrderReceiver afsOrderReceiver;

    /**
     * 售后历史
     */
    @TableField(exist = false)
    private List<AfsHistory> histories;
    /**
     * 逾期时间
     */
    @TableField(exist = false)
    private LocalDateTime overdueTime;

    /**
     * 买家手机号
     */
    @TableField(exist = false)
    private String buyerPhone;

    /**
     * 买家头像
     */
    @TableField(exist = false)
    private String buyerAvatar;


}
