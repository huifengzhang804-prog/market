package com.medusa.gruul.goods.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.goods.api.entity.ShopFollowNewProducts;
import com.medusa.gruul.goods.api.model.dto.ProductBrowseDTO;

/**
 *
 * 浏览的上新商品 服务类
 *
 *
 * @author WuDi
 * @since 2022-09-02
 */
public interface IShopFollowNewProductsService extends IService<ShopFollowNewProducts> {

    /**
     * 浏览商品记录
     * @param productBrowseDTO 浏览商品DTO
     */
    void productBrowse(ProductBrowseDTO productBrowseDTO);
}
