package com.medusa.gruul.cart.service.service;

import com.medusa.gruul.cart.api.dto.ShopIdSkuIdsDTO;
import com.medusa.gruul.cart.service.model.dto.ProductSkuDTO;
import com.medusa.gruul.cart.service.model.vo.ProductSkuVO;

import java.util.List;
import java.util.Map;

/**
 * 购物车服务接口
 *
 * @author 张治保
 * date 2022/5/16
 */
public interface CartService {

    /**
     * 添加购物车
     *
     * @param productSku 添加到购物车的sku信息
     */
    void addCart(ProductSkuDTO productSku);

    /**
     * 修改购物商品sku
     * 1. 修改选中的sku
     * 2. 修改sku数量
     *
     * @param uniqueId   skuId 更改前的skuId
     * @param productSku 更改后的sku信息
     */
    void editCartProductSku(String uniqueId, ProductSkuDTO productSku);

    /**
     * 删除购物车部分商品
     *
     * @param userId          删除指定userId对应的用户的购物车
     * @param shopId          店铺id
     * @param shopProductSkus 购物车店铺商品列表
     */
    void deleteCartProduct(Long userId, Long shopId, List<ShopIdSkuIdsDTO> shopProductSkus);

    /**
     * 清空购物车
     *
     * @param shopId 店铺id
     */
    void clear(Long shopId);

    /**
     * 清空失效商品
     *
     * @param shopId 店铺id
     */
    void clearInvalid(Long shopId);

    /**
     * 店铺加购数 更新
     *
     * @param shopCountMap 店铺购物车计数map
     */
    void updateShopCount(Map<Long, List<ProductSkuVO>> shopCountMap);
}
