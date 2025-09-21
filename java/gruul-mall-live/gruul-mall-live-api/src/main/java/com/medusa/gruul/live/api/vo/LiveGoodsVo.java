package com.medusa.gruul.live.api.vo;

import com.medusa.gruul.live.api.enums.AuditStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author miskw
 * @date 2022/11/16
 */
@Data
public class LiveGoodsVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 微信商品标识符
     */
    private Long goodsId;

    /**
     * 审核状态 0：未审核，1：审核中，2:审核通过，3审核未通过，4：违规下架
     */
    private AuditStatus auditStatus;

    /**
     * 审核时间
     */
    private LocalDateTime verifyTime;

    /**
     * 店铺标识符
     */
    private Long shopId;

    /**
     * 商品封面图
     */
    private String ossImgUrl;

    /**
     *价格类型，
     * 1：一口价（只需要传入price，price2不传）
     * 2：价格区间（price字段为左边界，price2字段为右边界，price和price2必传）
     * 3：显示折扣价（price字段为原价，price2字段为现价， price和price2必传）
     */
    private Integer priceType;

    /**
     * 价格左边界
     */
    private Long price;

    /**
     * 价格右边界
     */
    private Long price2;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
