package com.medusa.gruul.addon.bargain.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.bargain.model.enums.BargainStatus;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesValueDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 砍价订单
 *
 * @author WuDi
 * @since 2023-03-14
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_bargain_order", autoResultMap = true)
public class BargainOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 发起人id
     */
    private Long sponsorId;

    /**
     * 用户头像
     */
    private String userHeadPortrait;

    /**
     * 用户昵称
     */
    private String userNickname;


    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品skuId
     */
    private Long skuId;

    /**
     * 商品图片
     */
    private String productPic;

    /**
     * 商品名称
     */
    private String productName;


    /**
     * sku 名称
     */
    private String skuName;

    /**
     * 实售价
     */
    private Long salePrice;

    /**
     * 砍价状态
     */
    private BargainStatus bargainStatus;

    /**
     * 底价
     */
    private Long floorPrice;

    /**
     * 砍价人数
     */
    private Integer bargainingPeople;

    /**
     * 发布砍价时间
     */
    private LocalDateTime publishBargainingTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 产品属性
     */
    @TableField(value = "product_attributes", typeHandler = Fastjson2TypeHandler.class)
    private Set<ProductFeaturesValueDTO> productAttributes;
}
