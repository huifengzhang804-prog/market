package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * 商品标签
 *
 * @author wufuzhong
 * @Date 2022-12-02 10:30:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_product_label", autoResultMap = true)
public class ProductLabel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 店铺id
     */
    @TableField("shop_id")
    private Long shopId;

    /**
     * 标签名称
     */
    @TableField("name")
    private String name;

    /**
     * 字体颜色
     */
    @TableField("font_color")
    private String fontColor;

    /**
     * 背景颜色
     */
    @TableField("background_color")
    private String backgroundColor;

    /**
     * 商家类型
     */
    @TableField(value = "shop_type", typeHandler = Fastjson2TypeHandler.class)
    private List<ShopType> shopType;

}
