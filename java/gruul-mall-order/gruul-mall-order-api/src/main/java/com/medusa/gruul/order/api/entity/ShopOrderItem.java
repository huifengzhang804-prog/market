package com.medusa.gruul.order.api.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.enums.ServiceBarrier;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import com.medusa.gruul.order.api.enums.DeliverType;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.api.enums.PackageStatus;
import com.medusa.gruul.order.api.model.ItemExtra;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 张治保
 * @since 2022-06-08
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_shop_order_item", autoResultMap = true)
public class ShopOrderItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单商品状态
     */
    @TableField("`status`")
    private ItemStatus status;

    /**
     * 售后工单号
     */
    private String afsNo;

    /**
     * 售后状态
     */
    private AfsStatus afsStatus;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 供应商id
     */
    private Long supplierId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;
    /**
     * 商品类型
     */
    @TableField(value = "product_type")
    private ProductType productType;


    /**
     * 商品服务保障
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<ServiceBarrier> services;

    /**
     * 包裹id 发货时对应关系
     */
    private Long packageId;

    /**
     * 包裹状态
     */
    private PackageStatus packageStatus;

    /**
     * 商品 sku Id
     */
    private Long skuId;

    /**
     * 商品规格
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> specs;

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * 该商品sku 图片
     */
    private String image;

    /**
     * 单件商品重量
     */
    private BigDecimal weight;

    /**
     * 运费模板id
     */
    private Long freightTemplateId;

    /**
     * 运费
     */
    private Long freightPrice;

    /**
     * 销售单价 (商品价、秒杀等活动设置的价格)
     */
    private Long salePrice;

    /**
     * 成交价(扣掉折扣后的单价)
     */
    private Long dealPrice;

    /**
     * 修正价格  除不尽时的保留价 以当前上商品总额计算 修正价格 = 商品总额 - ((商品总额 / 商品数量) * 商品数量)
     */
    private Long fixPrice;

    /**
     * 商品销售类型
     */
    private SellType sellType;

    /**
     * 供应商店铺类型
     */
    @TableField(exist = false)
    private ShopType supplierShopType;

    /**
     * 额外数据
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private ItemExtra extra;

    /**
     * 配送方式
     */
    @TableField(exist = false)
    private DeliverType deliverType;
    /**
     * 关联店铺订单包裹信息 以便处理数据
     */
    @ToString.Exclude
    @TableField(exist = false)
    private ShopOrder shopOrder;

    /**
     * sku key 一个对象只生成一次 用于缓存提升效率
     */
    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @TableField(exist = false)
    @JSONField(serialize = false, deserialize = false)
    private ShopProductSkuKey key;

    /**
     * 获取商品总价
     */
    public long totalPrice() {
        return this.salePrice * this.num;
    }

    /**
     * 获取当前 sku商品支付总价 ,不包含运费
     *
     * @return long 商品总价
     */
    public long payPrice() {
        return (this.dealPrice * this.num) + this.fixPrice;
    }


    /**
     * 获取商品sku key
     *
     * @return com.medusa.gruul.common.model.base.ShopProductSkuKey 商品sku key
     */
    public ShopProductSkuKey shopProductSkuKey() {
        if (key != null) {
            return key;
        }
        return key = new ShopProductSkuKey().setShopId(getShopId()).setProductId(getProductId()).setSkuId(getSkuId());
    }


}
