package com.medusa.gruul.storage.service.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.storage.api.vo.ProductSpecsSkusVO;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;
import com.medusa.gruul.storage.service.service.StorageShopProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品统计
 *
 * @author 张治保 date 2022/7/13
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/storage/shop/{shopId}/product")
public class StorageShopProductController {


    private final StorageShopProductService storageShopProductService;

    /**
     * 查询商品统计信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品统计信息
     */
    @GetMapping("/{productId}/statistics")
    public Result<ProductStatisticsVO> getProductStatistics(@PathVariable Long shopId, @PathVariable Long productId) {
        return Result.ok(
                storageShopProductService.getProductStatistics(shopId, productId)
        );
    }

    /**
     * 查询商品规格sku信息
     *
     * @param shopId     店铺id
     * @param productId  商品id
     * @param isSupplier 是否查询供应商商品sku
     * @return 商品统计信息
     */
    @Log("查询商品规格组与sku")
    @GetMapping("/{productId}")
    public Result<ProductSpecsSkusVO> getProductSpecsSkus(
            @PathVariable Long shopId, @PathVariable Long productId,
            @RequestParam(required = false, defaultValue = "false") boolean isSupplier
    ) {
        return Result.ok(
                storageShopProductService.getProductSpecsSkus(shopId, productId, isSupplier)
        );
    }


}
