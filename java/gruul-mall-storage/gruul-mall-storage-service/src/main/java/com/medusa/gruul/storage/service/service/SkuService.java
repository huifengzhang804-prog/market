package com.medusa.gruul.storage.service.service;


import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.storage.api.dto.ProductSkuStockDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.service.model.dto.ProductSkuLimitDTO;
import com.medusa.gruul.storage.service.model.dto.ProductSkuPriceDTO;

import java.util.List;

/**
 * @author 张治保
 * date 2022/6/22
 */
public interface SkuService {

    /**
     * 设置商品限购信息
     *
     * @param productId 商品id
     * @param limits    商品库存信息
     */
    void setProductLimit(Long productId, List<ProductSkuLimitDTO> limits);

    /**
     * 更新商品库存
     *
     * @param productId 商品id
     * @param skuStocks sku调整列表
     */
    void updateSkuStock(Long productId, List<ProductSkuStockDTO> skuStocks);


    /**
     * 获取商品sku仓储信息 先走缓存,缓存没有 查数据库 数据库有则写入缓存并返回
     *
     * @param shopProductSkuKey 店铺商品数据id
     * @return 商品数据缓存信息
     */
    StorageSku getProductSku(ActivityShopProductSkuKey shopProductSkuKey);


    /**
     * 更新商品sku价格
     *
     * @param productId       商品id
     * @param productSkuPrice 商品sku价格
     */
    void updateSkuPrice(Long productId, List<ProductSkuPriceDTO> productSkuPrice);

}
