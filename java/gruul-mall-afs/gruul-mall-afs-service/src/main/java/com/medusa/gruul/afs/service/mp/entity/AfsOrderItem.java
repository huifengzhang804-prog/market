package com.medusa.gruul.afs.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.global.model.enums.ServiceBarrier;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 售后工单商品
 * </p>
 *
 * @author 张治保
 * @since 2022-08-03
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
@TableName("t_afs_order_item")
public class AfsOrderItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 售后工单号
     */
    private String afsNo;

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
    @TableField("product_type")
    private ProductType productType;

    /**
     * sku id
     */
    private Long skuId;

    /**
     * sku规格
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> specs;

    /**
     * sku图片
     */
    private String image;

    /**
     * 商品数量
     */
    private Integer num;
    /**
     * 销售类型
     */
    @TableField("`sell_type`")
    private SellType sellType;

    /**
     * 商品保障服务
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<ServiceBarrier> services;

    /**
     * 销售单价
     */
    private Long salePrice;

    /**
     * 成交价单价
     */
    private Long dealPrice;


}
