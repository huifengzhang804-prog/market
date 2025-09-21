package com.medusa.gruul.live.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.live.api.enums.AuditStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * @author miskw
 * @date 2022/11/8
 * @describe 直播商品审核
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_live_goods_examine")
public class LiveGoodsExamine extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 商品名称
     */
    @TableField("product_name")
    private String productName;

    /**
     * 微信商品标识符
     */
    @TableField("goods_id")
    private Long goodsId;


    /**
     * 审核状态 0：未审核，1：审核中，2:审核通过，3审核未通过，4：违规下架
     */
    @TableField("audit_status")
    private AuditStatus auditStatus;

    /**
     * 审核单ID
     */
    @TableField("audit_id")
    private Long auditId;

    /**
     *  店铺标识符
     */
    @TableField("shop_id")
    private Long shopId;

    /**
     * 店铺名称
     */
    @TableField("shop_name")
    private String shopName;

    /**
     * oss商品封面图
     */
    @TableField("oss_img_url")
    private String ossImgUrl;

    /**
     * 微信图片mediaID
     */
    @TableField("cover_img_url")
    private String coverImgUrl;

    /**
     * 提交审核时间
     */
    @TableField("verify_time")
    private LocalDateTime verifyTime;

    /**
     *价格类型，
     * 1：一口价（只需要传入price，price2不传）
     * 2：价格区间（price字段为左边界，price2字段为右边界，price和price2必传）
     * 3：显示折扣价（price字段为原价，price2字段为现价， price和price2必传）
     */
    @TableField("price_type")
    private Integer priceType;

    /**
     * 价格左边界
     */
    @TableField("price")
    private Long price;

    /**
     * 价格右边界
     */
    @TableField("price2")
    private Long price2;

    /**
     * 商品路径
     */
    @TableField("url")
    private String url;





}
