package com.medusa.gruul.live.service.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author miskw
 * @date 2022/11/10
 * @describe 添加并提审商品
 */
@Data
public class AddGoodsDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 店铺Id
     */
    private Long shopId;

    /**
     * 商品Id
     */
    private Long productId;

    /**
     * 微信商品Id
     */
    private Long goodsId;

    /**
     * mediaID 图片规则：图片尺寸最大300像素*300像素；
     */
    @NotBlank(message = "请上传图片")
    private String coverImgUrl;

    /**
     * oss商品封面图
     */
    private String ossImgUrl;

    /**
     * 商品名称
     */
    @NotBlank(message = "请输入商品名称")
    private String productName;
    /**
     * 价格类型，1：一口价（只需要传入price，price2不传）
     * 2：价格区间（price字段为左边界，price2字段为右边界，price和price2必传）
     * 3：显示折扣价（price字段为原价，price2字段为现价， price和price2必传）
     */
    private Number priceType;
    /**
     * 价格左区间
     */
    private Long price;
    /**
     * 价格右区间
     */
    private Long price2;
    /**
     * 商品详情页的小程序路径，路径参数存在 url 的，该参数的值需要进行 encode 处理再填入
     */
    @NotBlank(message = "请填写商品路径")
    private String url;


}
