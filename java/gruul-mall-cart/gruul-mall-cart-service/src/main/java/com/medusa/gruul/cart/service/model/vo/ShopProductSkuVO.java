package com.medusa.gruul.cart.service.model.vo;

import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * 店铺与该店铺选购商品列表
 * @author 张治保
 * date 2022/5/16
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopProductSkuVO {
    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 店铺名称
     */
    private String shopName;
    /**
     * 店铺模式
     */
    private ShopMode shopMode;
    /**
     * 店铺类型
     */
    private ShopType shopType;
    /**
     *
     */
    private String shopLogo;
    /**
     * 店铺是否可用
     */
    private Boolean enable;
    /**
     * 该店铺选购的商品
     */
    private List<ProductSkuVO> products;

    public ShopProductSkuVO(){
        this.products = new ArrayList<>();
    }
}
