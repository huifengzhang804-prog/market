package com.medusa.gruul.addon.seckill.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/5/30
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SeckillProductVO {

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

    /**
     * 商品 sku
     */
    private List<SeckillSkuVO> skus;

}
