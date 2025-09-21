package com.medusa.gruul.user.api.model.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author WuDi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserFootMarkDTO implements Serializable {

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 商品id
     */
    @NotNull(message = "商品id不能为空")
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
     * 三级分类id
     */
    private Long platformCategoryId;
}
