package com.medusa.gruul.addon.seckill.model.bo;

import lombok.*;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/5/30
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProductKey implements Serializable {
    /**
     * 商品 id
     */
    private Long id;
    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品图片
     */
    private String image;
}
