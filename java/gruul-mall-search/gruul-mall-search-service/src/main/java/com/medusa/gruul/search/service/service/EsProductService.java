package com.medusa.gruul.search.service.service;

import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductBroadcastDTO;
import com.medusa.gruul.goods.api.model.dto.ProductDeleteDTO;
import com.medusa.gruul.goods.api.model.dto.ProductNameUpdateDTO;
import com.medusa.gruul.goods.api.model.dto.ProductUpdateStatusDTO;
import com.medusa.gruul.search.api.enums.BrandStatus;
import com.medusa.gruul.search.api.model.CategoryCountParam;
import com.medusa.gruul.search.api.model.CategoryStaticVo;
import com.medusa.gruul.search.api.model.NestedCategory;
import com.medusa.gruul.search.service.es.entity.EsProductEntity;
import com.medusa.gruul.search.service.model.SearchParam;
import com.medusa.gruul.search.service.model.dto.SuggestDTO;
import com.medusa.gruul.search.service.model.vo.ShopProductSalesTopVO;
import com.medusa.gruul.shop.api.model.dto.ShopsEnableDisableDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.dto.ProductPriceUpdateDTO;
import com.medusa.gruul.storage.api.vo.ProductSkusVO;
import org.dromara.easyes.core.biz.EsPageInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/12/1
 */
public interface EsProductService {

    /**
     * 店铺状态改变
     *
     * @param shopStatus 店铺状态列表
     */
    void shopStatusChange(List<ShopsEnableDisableDTO> shopStatus);

    /**
     * 商品发布
     *
     * @param productRelease 商品发布
     */
    void productRelease(ProductBroadcastDTO productRelease);

    /**
     * 商品更新
     *
     * @param productUpdate 商品更新
     */
    void productUpdate(ProductBroadcastDTO productUpdate);

    /**
     * 更新商品状态
     *
     * @param updateStatus 更新商品状态
     */
    void productStatusUpdate(List<ProductUpdateStatusDTO> updateStatus);


    /**
     * 店铺商品批量删除
     *
     * @param productDelete 批量删除店铺商品
     */
    void productDelete(ProductDeleteDTO productDelete);

    /**
     * 分页查询商品
     *
     * @param isConsumer 是否是消费者端
     * @param param      分页查询参数
     * @return 分页查询结果
     */
    EsPageInfo<EsProductEntity> search(boolean isConsumer, SearchParam param);

    /**
     * 分类商品数统计
     *
     * @param param 统计参数
     * @return 分类商品数统计 key 分类 id（一级｜二级 ｜ 三级），value 商品数
     */
    Map<Long, CategoryStaticVo> categoryCount(CategoryCountParam param);

    /**
     * 更新商品库存与销量
     *
     * @param productIdAndSkuStSv sku key与 库存销量对应信息
     */
    void updateProductStockAndSales(Map<String, Map<Long, StSvBo>> productIdAndSkuStSv);


    /**
     * 搜索建议
     *
     * @param suggest 搜索建议参数
     * @return 搜索建议
     */
    List<EsProductEntity> suggest(SuggestDTO suggest);


    /**
     * 商品分类清空
     *
     * @param nestedCategory 类目信息
     */
    void productClassifyEmpty(NestedCategory nestedCategory);

    /**
     * 更新品牌状态
     *
     * @param brandId 品牌id
     * @param status  品牌状态
     */
    void brandStatusUpdate(Long brandId, BrandStatus status);

    /**
     * 删除品牌
     *
     * @param brandId 品牌id
     */
    void brandDelete(Long brandId);

    /**
     * 获取商家销量最高的6个商品
     *
     * @param shopIds 商家id集合
     * @return 商家销量最高的6个商品
     */
    List<ShopProductSalesTopVO> getShopProductSalesTop(List<Long> shopIds);

    /**
     * 多规格价格修改
     *
     * @param priceUpdate 商品价格更新DTO
     */
    void updateSkuPrice(ProductPriceUpdateDTO priceUpdate);

    /**
     * 店铺信息变更，更新商品信息中的店铺名称和店铺类型
     *
     * @param shopInfo 店铺信息
     */
    void updateProductShopInfo(ShopInfoVO shopInfo);

    /**
     * 商品标签删除
     *
     * @param productList 商品list
     */
    void productLabelDelete(List<Product> productList);

    /**
     * 更新商品名称
     *
     * @param productName 商品名称数据
     */
    void productNameUpdate(ProductNameUpdateDTO productName);
    /**
     * 更新商品标签
     *
     * @param product 商品
     */
    void updateProductLabel(Product product);
}
