package com.medusa.gruul.goods.api.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.storage.api.entity.StorageSku;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 店铺商品基础Vo
 * @Author: xiaoq
 * @Date : 2022-05-19
 */
@Data
public class ApiProductVO implements Serializable {

    private Long id;

    private Long shopId;
    /**
     * 店铺类型
     */
    private ShopType shopType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 供应商名称
     */
    private String providerName;

    /**
     * "展示图片"
     */
    private String pic;
    /**
     * 画册图片，连产品图片限制为6张，以逗号分割
     */
    private String albumPics;

    @TableField(value = "sale_prices", typeHandler = Fastjson2TypeHandler.class)
    private List<Long> salePrices;

    /**
     * 商品 仓储
     */
    private List<StorageSku> storageSkus;
    /**
     * 销量
     */
    private Long salesCount;

    /**
     * 运费模版id
     */
    private Long freightTemplateId;
    /**
     * 是否包邮
     */
    private Boolean freeShipping = Boolean.FALSE;
    /**
     * 规格图
     */
    private List<String> specImages;

    /**
     * 优惠券
     */
    private List<GoodsCouponVO> coupons;
}
