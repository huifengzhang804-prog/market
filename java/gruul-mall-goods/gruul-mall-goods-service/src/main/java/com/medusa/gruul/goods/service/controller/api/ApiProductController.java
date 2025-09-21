package com.medusa.gruul.goods.service.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.custom.aggregation.classify.dto.CategoryRankDTO;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.goods.api.model.param.ApiProductParam;
import com.medusa.gruul.goods.api.model.param.PlatformCategoryParam;
import com.medusa.gruul.goods.api.model.param.ProductRandomParam;
import com.medusa.gruul.goods.api.model.vo.*;
import com.medusa.gruul.goods.service.model.dto.ProductDetailDTO;
import com.medusa.gruul.goods.service.model.dto.ShopProductSkuIdDTO;
import com.medusa.gruul.goods.service.model.vo.ProductDeliveryVO;
import com.medusa.gruul.goods.service.model.vo.ProductDetailVO;
import com.medusa.gruul.goods.service.mp.service.IProductService;
import com.medusa.gruul.storage.api.vo.ProductSaleVolumeVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序商品信息 前端控制器
 *
 * @author xiaoq
 * @since 2019-10-06
 */
@Validated
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ApiProductController {

    private final IProductService apiProductService;


    /**
     * 商品详情
     *
     * @param id 商品id
     */
    @GetMapping("/get/{id}")
    public Result<ProductVO> getProductById(@PathVariable("id") Long id, Long shopId) {
        if (shopId == null) {
            shopId = ISystem.shopIdOpt().get();
        }
        ProductVO product = apiProductService.getProductById(id, shopId);
        return Result.ok(product);
    }

    /**
     * 商品详情,聚合C端商品页面接口（商品信息、库存、活动、套餐、满减、优惠券、会员信息、分销佣金、返利、是否收藏、）
     *
     * @param param 商品详情查询参数
     * @return 商品详情聚合参数
     */
    @PostMapping("/details")
    public Result<ProductDetailVO> details(@RequestBody @Valid ProductDetailDTO param) {
        param.validParam();
        return Result.ok(
                apiProductService.details(param)
        );
    }


    /**
     * 商品详情，sku切换
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @param skuId     skuId
     * @param salePrice 销售价
     */
    @GetMapping("/change/sku/{shopId}/{productId}/{skuId}/{salePrice}")
    public Result<ProductVO> getProductChangeSkuInfo(@PathVariable("shopId") Long shopId,
                                                     @PathVariable("productId") Long productId, @PathVariable("skuId") Long skuId,
                                                     @PathVariable("salePrice") Long salePrice) {
        ProductVO product = apiProductService.getProductChangeSkuInfo(
                shopId, productId, skuId, salePrice
        );
        return Result.ok(product);
    }

    /**
     * 店铺商品列表
     *
     * @param apiProductParam 查询条件
     * @return <店铺商品基础信息>
     */
    @Log("店铺商品列表")
    @GetMapping("list")
    public Result<Page<ApiProductVO>> getProductInfoByParam(ApiProductParam apiProductParam) {
        Page<ApiProductVO> apiProductVoPage = apiProductService.getProductInfoByParam(apiProductParam);
        return Result.ok(apiProductVoPage);
    }

    /**
     * 根据平台三级类目Id 获取商品信息List
     *
     * @param platformCategoryParam 查询条件
     * @return <平台基础商品信息>
     */
    @Log("根据平台类目三级Id 获取所有商铺下的产品信息")
    @GetMapping("/by/thirdly/category/list/")
    public Result<Page<ApiPlatformProductVO>> getProductInfoByPlatformThirdlyCategoryId(
            PlatformCategoryParam platformCategoryParam) {
        Page<ApiPlatformProductVO> productInfo = apiProductService.getProductInfoByPlatformThirdlyCategoryId(
                platformCategoryParam);
        return Result.ok(productInfo);
    }


    /**
     * 获取店铺在售商品数量
     *
     * @return 在售商品数量
     */
    @Log("获取店铺在售商品数量")
    @GetMapping("/shop/{shopId}/count")
    public Result<Long> getShopSalesProductCount(@PathVariable Long shopId) {
        return Result.ok(apiProductService.getShopSalesProductCount(shopId));
    }

    /**
     * 获取商品基础信息 By categoryId
     *
     * @param categoryRank 类目级别dto
     * @return Page<ApiProductVO>
     */
    @Log("获取商品基础信息 By categoryId")
    @GetMapping("by/categoryId")
    public Result<Page<ApiProductVO>> getProductInfoByCategoryId(CategoryRankDTO categoryRank) {
        return Result.ok(apiProductService.getProductInfoByCategoryId(categoryRank));
    }

    /**
     * pc端-看了又看
     *
     * @param productRandomParam 分页参数
     * @return 看了又看商品
     */
    @Log("pc端-看了又看")
    @GetMapping("/lookAndSee")
    public Result<Page<ApiProductLookAndSeeVO>> lookAndSeePage(ProductRandomParam productRandomParam) {
        return Result.ok(
                apiProductService.lookAndSeePage(productRandomParam)
        );
    }

    /**
     * pc端-店铺热销
     *
     * @return 店铺热销商品
     */
    @Log("pc端-店铺热销")
    @GetMapping("shopHotSales")
    public Result<List<ProductSaleVolumeVO>> shopHotSales() {
        return Result.ok(
                apiProductService.shopHotSales(ISystem.shopIdMust(), 5L)
        );
    }

    /**
     * pc端-热门关注
     *
     * @return 热门关注商品
     */
    @Log("pc端-热门关注")
    @GetMapping("popularAttention")
    public Result<List<ApiProductPopularAttentionVO>> shopPopularAttention() {
        return Result.ok(
                apiProductService.shopPopularAttention()
        );
    }

    /**
     * 查询运费相关信息
     *
     * @param productSkuIds 店铺商品id
     * @return 运费相关信息
     */
    @Log("查询运费相关信息")
    @PostMapping("/productDelivery")
    public Result<List<ProductDeliveryVO>> getProductDelivery(
            @RequestBody @Valid @Size(min = 1) List<ShopProductSkuIdDTO> productSkuIds) {
        return Result.ok(
                apiProductService.getProductDelivery(productSkuIds)
        );
    }

}
