package com.medusa.gruul.user.service.model.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author WuDi
 */
@Getter
@Setter
@ToString
public class UserCollectDTO {

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 商品id
     */
    @NotNull
    private Long productId;

    /**
     * 展示图片
     */
    private String productPic;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品价格
     */
    private Long productPrice;


    /**
     * 供应商id
     */
    private Long supplierId;


}
