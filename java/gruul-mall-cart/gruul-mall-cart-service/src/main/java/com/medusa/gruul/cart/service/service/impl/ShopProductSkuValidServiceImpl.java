package com.medusa.gruul.cart.service.service.impl;

import com.medusa.gruul.cart.api.constant.CartErrorCode;
import com.medusa.gruul.cart.service.model.bo.ShopProductSkuValid;
import com.medusa.gruul.cart.service.service.ShopProductSkuValidService;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesValueDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.StockType;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/7/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShopProductSkuValidServiceImpl implements ShopProductSkuValidService {

    private final GoodsRpcService goodsRpcService;
    private final StorageRpcService storageRpcService;

    private final ShopRpcService shopRpcService;

    @Override
    public ShopProductSkuValid validShop(Long shopId) {
        ShopProductSkuValid validResult = new ShopProductSkuValid();
        ShopInfoVO shopInfo = shopRpcService.getShopInfoByShopId(shopId);
        validResult.setShopInfo(shopInfo);
        //店铺不可用
        if (shopInfo == null || ShopStatus.NORMAL != shopInfo.getStatus()) {
            validResult.setException(
                    new GlobalException(CartErrorCode.INVALID_SHOP, "店铺不可用")
            );
        }
        return validResult;
    }

    @Override
    public ShopProductSkuValid validShopProductSku(Long shopId, Long productId, Long skuId, int num, Set<ProductFeaturesValueDTO> productAttribute) {

        ActivityShopProductSkuKey shopProductSkuKey = new ActivityShopProductSkuKey().setSkuId(skuId);
        shopProductSkuKey.setProductId(productId).setShopId(shopId).setActivityType(OrderType.COMMON).setActivityId(0L);
        ShopProductSkuValid result = new ShopProductSkuValid();
        /* 检查商品
         */
        Product productInfo = goodsRpcService.getProductInfo(shopId, productId);
        result.setProductInfo(productInfo);
        if (productInfo == null || ProductStatus.SELL_ON != productInfo.getStatus()) {
            return result.setException(
                    new GlobalException("商品不可用", CartErrorCode.INVALID_PRODUCT, shopProductSkuKey)
            );
        }
        /* 检查SKU
         */
        /* 检查商品 检查限购  检查 库存
         */
        if (SellType.CONSIGNMENT.equals(productInfo.getSellType())) {
            //属于代销商品查询SKU时需要用供应商的id去查
            shopProductSkuKey.setShopId(productInfo.getSupplierId());
        }
        StorageSku storageSku = storageRpcService.getProductSku(shopProductSkuKey);
        result.setStorageSku(storageSku);
        if (storageSku == null) {
            return result.setException(
                    new GlobalException("商品SKU不可用", CartErrorCode.INVALID_SKU, shopProductSkuKey)
            );
        }
        if (productAttribute != null) {
            try {
                productInfo.getExtra().checkProductAttributes(ProductFeaturesValueDTO.userSelectedForm(productAttribute));
            } catch (GlobalException e) {
                return result.setException(e);
            }
        }

        /* 检查库存
         */
        Long stock = storageSku.getStock();
        if (StockType.LIMITED == storageSku.getStockType() && stock < num) {
            return result.setException(
                    stock <= 0 ? new GlobalException("商品售罄", CartErrorCode.SOLD_OUT, shopProductSkuKey)
                            : new GlobalException("商品库存不足", CartErrorCode.OUT_OF_STOCK, shopProductSkuKey)
            );
        }

        return result;
    }

}
