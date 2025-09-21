package com.medusa.gruul.cart.service.service;

import com.medusa.gruul.cart.service.model.bo.ShopProductSkuValid;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesValueDTO;

import java.util.Set;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/7/23
 */
public interface ShopProductSkuValidService {

    /**
     * 校验店铺是否可用
     * @param shopId 店铺id
     * @return 校验结果
     */
    ShopProductSkuValid validShop(Long shopId);
    /**
     * 校验 商品sku
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @param skuId     sku id
     * @param num 选购数量
     * @param productAttribute 选购商品属性
     * @return 校验结果 null 校验通过 不为空校验不通过
     */
    ShopProductSkuValid validShopProductSku(Long shopId, Long productId, Long skuId, int num, Set<ProductFeaturesValueDTO> productAttribute);
}
