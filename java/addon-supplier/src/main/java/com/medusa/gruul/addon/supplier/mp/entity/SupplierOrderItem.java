package com.medusa.gruul.addon.supplier.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.supplier.model.bo.OrderItemExtra;
import com.medusa.gruul.addon.supplier.model.enums.PackageStatus;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 供应商订单商品项实体类
 *
 * @author 张治保
 * @since 2023/7/19
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_supplier_order_item", autoResultMap = true)
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

    /**
     * 额外数据信息
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private OrderItemExtra extra;

    /**
     * 已入库数量
     */
    @TableField(exist = false)
    private Integer used;

    /**
     * copy item 订单拆分时使用
     */
    public SupplierOrderItem copyItem(Integer num) {
        SupplierOrderItem item = new SupplierOrderItem();
        item.setOrderNo(this.orderNo)
                .setSupplierId(this.supplierId)
                .setPackageId(this.packageId)
                .setPackageStatus(this.packageStatus)
                .setProductId(this.productId)
                .setProductName(this.productName)
                .setSkuId(this.skuId)
                .setNum(num)
                .setImage(this.image)
                .setSpecs(this.specs)
                .setSalePrice(this.salePrice)
                .setDealPrice(this.dealPrice)
                .setFixPrice(0L)
                .setFreightPrice(0L)
                .setExtra(this.extra)
                .setCreateTime(this.getCreateTime())
                .setUpdateTime(this.getUpdateTime());
        return item;
    }

}
