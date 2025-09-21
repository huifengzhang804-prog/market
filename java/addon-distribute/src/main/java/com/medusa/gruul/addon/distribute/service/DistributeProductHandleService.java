package com.medusa.gruul.addon.distribute.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.distribute.model.dto.ProductBindDTO;
import com.medusa.gruul.addon.distribute.model.dto.ProductQueryDTO;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeProduct;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductUpdateStatusDTO;
import com.medusa.gruul.storage.api.dto.ProductPriceUpdateDTO;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保 date 2022/11/14
 */
public interface DistributeProductHandleService {


    /**
     * 新增分销商品绑定
     *
     * @param shopId      店铺id
     * @param productBind 绑定参数
     */
    void newProduct(Long shopId, ProductBindDTO productBind);

    /**
     * 修改分销商品绑定关系
     *
     * @param shopId      店铺id
     * @param bindId      绑定关系id
     * @param productBind 绑定参数
     */
    void editProduct(Long shopId, Long bindId, ProductBindDTO productBind);

    /**
     * 批量删除分销商品
     *
     * @param shopId  店铺id
     * @param bindIds 分销商品绑定关系id集合
     */
    void deleteBatch(Long shopId, Set<Long> bindIds);


    /**
     * 批量取消分销商品
     *
     * @param shopId  店铺id
     * @param bindIds 分销商品绑定关系id集合
     */
    void cancelBatch(Long shopId, Set<Long> bindIds);

    /**
     * 分页查询分销商品
     *
     * @param query 分页查询条件
     * @return 分销商品分页列表
     */
    IPage<DistributeProduct> productPage(ProductQueryDTO query);


    /**
     * 更新商品信息
     *
     * @param product 商品信息
     */
    void updateProductByProduct(Product product);


    /**
     * 更新商品状态
     *
     * @param shopProductsList 店铺id与 商品id集合 状态
     */
    void updateProductStatus(List<ProductUpdateStatusDTO> shopProductsList);


    /**
     * 查询店铺商品配置
     *
     * @param shopProductKeys 店铺商品key集合
     * @return 店铺商品配置
     */
    Map<ShopProductKey, DistributeProduct> shopProductConfMap(Set<ShopProductKey> shopProductKeys);


    /**
     * 更新商品销量
     *
     * @param productSales 商品销量对应关系
     */
    void updateProductSales(Map<ShopProductKey, Long> productSales);

    /**
     * 商品预计算 返回null 代表不展示
     *
     * @param key    商品key
     * @param userId 指定用户 id,未指定时可以为空
     * @return 分红金额
     */
    Long productPrecompute(ShopProductKey key, @Nullable Long userId);

    /**
     * 重新分销
     *
     * @param bindId 分销商品绑定关系id
     */
    void againDistribute(Long shopId, Long bindId);

    /**
     * 商品sku修改价格 同步 修正 sale_prices
     *
     * @param priceUpdate 商品价格更新DTO
     */
    void updateProductSkuPrice(ProductPriceUpdateDTO priceUpdate);

}
