package com.medusa.gruul.addon.bargain.model.dto;

import com.medusa.gruul.goods.api.model.dto.ProductFeaturesValueDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * 砍价发起人信息
 *
 * @author wudi
 */
@Getter
@Setter
@ToString
public class BargainSponsorDTO {

    /**
     * 店铺id
     */
    @NotNull(message = "店铺id不能为空")
    private Long shopId;

    /**
     * 商品id
     */
    @NotNull(message = "商品id不能为空")
    private Long productId;

    /**
     * 商品skuId
     */
    @NotNull(message = "商品skuId不能为空")
    private Long skuId;

    /**
     * 商品sku图片
     */
    @NotBlank(message = "商品sku图片不能为空")
    private String skuImage;

    /**
     * 活动id
     */
    @NotNull(message = "活动id不能为空")
    private Long activityId;


    /**
     * 产品属性
     */
    private Set<ProductFeaturesValueDTO> productFeaturesValue;

    /**
     * 用户头像
     */
    private String userHeadPortrait;

    /**
     * 用户昵称
     */
    private String userNickname;

}
