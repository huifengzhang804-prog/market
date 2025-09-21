package com.medusa.gruul.goods.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.goods.api.entity.ShopFollowNewProducts;
import com.medusa.gruul.goods.api.model.dto.ProductBrowseDTO;
import com.medusa.gruul.goods.service.mp.mapper.ShopFollowNewProductsMapper;
import com.medusa.gruul.goods.service.mp.service.IShopFollowNewProductsService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 浏览的上新商品 服务实现类
 *
 * @author WuDi
 * @since 2022-09-02
 */
@Service
@RequiredArgsConstructor
public class ShopFollowNewProductsServiceImpl extends ServiceImpl<ShopFollowNewProductsMapper, ShopFollowNewProducts> implements IShopFollowNewProductsService {


    /**
     * 浏览商品记录
     *
     * @param productBrowse 浏览商品
     */
    @Override
    public void productBrowse(ProductBrowseDTO productBrowse) {
        Option<SecureUser<Object>> userOpt = ISecurity.userOpt();
        if (userOpt.isEmpty()) {
            return;
        }
        Long userId = userOpt.get().getId();
        Long shopId = productBrowse.getShopId();
        Long productId = productBrowse.getProductId();
        Long count = this.lambdaQuery()
                .eq(ShopFollowNewProducts::getShopId, shopId)
                .eq(ShopFollowNewProducts::getUserId, userId)
                .eq(ShopFollowNewProducts::getProductId, productId)
                .count();
        if (count > 0) {
            this.lambdaUpdate()
                    .set(ShopFollowNewProducts::getUpdateTime, new Date())
                    .eq(ShopFollowNewProducts::getShopId, shopId)
                    .eq(ShopFollowNewProducts::getProductId, productId)
                    .eq(ShopFollowNewProducts::getUserId, userId)
                    .update();
            return;
        }
        ShopFollowNewProducts shopFollowNewProducts = new ShopFollowNewProducts();
        shopFollowNewProducts.setShopId(shopId)
                .setUserId(userId)
                .setProductId(productId)
                .setShopName(productBrowse.getShopName());
        this.save(shopFollowNewProducts);

    }
}
