package com.medusa.gruul.addon.invoice.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.invoice.model.enums.PackageStatus;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 供应商订单商品项实体类
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SupplierOrderItem extends BaseEntity {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 供应商 id
     */
    private Long supplierId;

    /**
     * 包裹ID
     */
    private Long packageId;

    /**
     * 包裹状态
     */
    private PackageStatus packageStatus;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * SKU图片
     */
    private String image;

    /**
     * 规格数据
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> specs;

    /**
     * 销售单价
     */
    private Long salePrice;

    /**
     * 成交单价
     */
    private Long dealPrice;

    /**
     * 修正价格
     */
    private Long fixPrice;

    /**
     * 运费
     */
    private Long freightPrice;

}
