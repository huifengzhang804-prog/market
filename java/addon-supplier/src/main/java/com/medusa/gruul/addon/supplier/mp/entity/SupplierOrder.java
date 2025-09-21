package com.medusa.gruul.addon.supplier.mp.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.supplier.model.bo.OrderExtra;
import com.medusa.gruul.addon.supplier.model.bo.OrderTimeNodes;
import com.medusa.gruul.addon.supplier.model.enums.InvoiceStatus;
import com.medusa.gruul.addon.supplier.model.enums.OrderStatus;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.mp.handler.type.MapToKeyValuesTypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.goods.api.entity.Product;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 供应商订单实体类
 *
 * @author 张治保
 * @since 2023/7/19
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "no", callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_supplier_order", autoResultMap = true, excludeProperty = "id")
public class SupplierOrder extends BaseEntity {

    /**
     * 订单号
     */
    @TableId(value = "`no`", type = IdType.INPUT)
    private String no;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 订单类型
     */
    private SellType type;

    /**
     * 订单状态
     */
    @TableField("`status`")
    private OrderStatus status;

    /**
     * 创建订单的用户店铺ID
     */
    private Long shopId;



    /**
     * 创建订单的用户ID
     */
    private Long shopUserId;

    /**
     * 需要支付的金额
     */
    private Long payAmount;

    /**
     * 主单号
     */
    private String mainNo;

    /**
     * 额外的其他信息
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private OrderExtra extra;

    /**
     * 时间节点信息
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private OrderTimeNodes timeNodes;

    /**
     * 订单购买的商品信息 key:商品ID value:商品信息
     */
    @TableField(typeHandler = MapToKeyValuesTypeHandler.class)
    private Map<Long, Product> products = new HashMap<>();

    /**
     * 订单供应商备注
     */
    private String remark;

    /**
     * 订单项
     */
    @TableField(exist = false)
    private List<SupplierOrderItem> orderItems;

    /**
     * 额外信息 用于存储临时查询数据
     */
    @TableField(exist = false)
    private JSONObject extraInfo;
    /**
     * 店铺名称
     */
    @TableField(exist = false)
    private String shopName;
    /**
     * 店铺联系电话
     */
    @TableField(exist = false)
    private String shopContractNumber;
    /**
     * 采购订单是否已经有商品入库
     */
    @TableField(exist = false)
    private boolean hasProductStorage;
    /**
     * 已入库数量
     */
    @TableField(exist = false)
    private Integer stockInCount;
    /**
     * 采购员手机号
     */
    @TableField(value = "purchase_phone")
    private String purchasePhone;
    /**
     * 采购员昵称
     */
    @TableField(exist = false)
    private String purchaseNickName;
    /**
     * 已发货数量
     */
    @TableField(exist = false)
    private Integer hasDeliveryCount;
    /**
     * 待发货数量
     */
    @TableField(exist = false)
    private Integer waitForDeliveryCount;
    /**
     * 发票状态
     */
    @TableField(value = "invoice_status")
    private InvoiceStatus invoiceStatus;
    /**
     * 发票申请时间
     */
    @TableField(value = "apply_invoice_time")
    private LocalDateTime applyInvoiceTime;
    /**
     * 开票时间
     */
    @TableField(value = "invoice_time")
    private LocalDateTime invoiceTime;




}