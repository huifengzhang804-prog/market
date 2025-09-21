package com.medusa.gruul.addon.bargain.vo;

import com.medusa.gruul.addon.bargain.model.enums.BargainSponsorSkuStatus;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesValueDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 砍价发起人商品sku信息
 *
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class BargainSponsorProductSkuVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 发起人id
     */
    private Long userId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 用户头像
     */
    private String userHeadPortrait;

    /**
     * 用户昵称
     */
    private String userNickName;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * 商品skuId
     */
    private Long skuId;

    /**
     * 商品sku名称
     */
    private String skuName;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品sku图片
     */
    private String skuImage;

    /**
     * 商品sku价格
     */
    private Long skuPrice;

    /**
     * 底价
     */
    private Long floorPrice;

    /**
     * 是否自我砍价
     */
    private Boolean isSelfBargain;

    /**
     * 已砍金额
     */
    private Long amountCut;

    /**
     * 砍价订单id
     */
    private Long bargainOrderId;

    /**
     * 是否可叠加优惠（会员，优惠券，满减）
     */
    private StackableDiscount stackable;


    /**
     * 砍价发起状态
     */
    private BargainSponsorSkuStatus bargainSponsorSkuStatus;

    /**
     * 产品属性
     */
    private Set<ProductFeaturesValueDTO> productAttributes;
}
