package com.medusa.gruul.storage.api.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author WuDi
 * date: 2022/11/9
 */
@Data
@Accessors(chain = true)
public class ProductSaleVolumeVO implements Serializable {

    /**
     * 店铺 id
     */
    private Long shopId;

    /**
     * 商品id
     */
    private Long productId;


    /**
     * 商品名称
     */
    private String productName;

    /**
     * 销量
     */
    private Long salesVolume;

    /**
     * 商品价格
     */
    private Long productPrice;

    /**
     * 展示图片
     */
    private String pic;

    /**
     * 已评价人数
     */
    private Long evaluated;

}
