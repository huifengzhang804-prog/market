package com.medusa.gruul.storage.service.controller;

import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.storage.api.dto.ProductSkuStockDTO;
import com.medusa.gruul.storage.api.dto.StorageSpecSkuDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.service.model.dto.ProductSkuLimitDTO;
import com.medusa.gruul.storage.service.model.dto.ProductSkuPriceDTO;
import com.medusa.gruul.storage.service.service.SkuService;
import com.medusa.gruul.storage.service.service.SkuStockService;
import com.medusa.gruul.storage.service.service.SpecSkuService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 商品仓储接口
 *
 * @author 张治保
 * date 2022/6/21
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/storage/product/{productId}")//
@PreAuthorize("""
        		@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.SHOP_ADMIN).
        		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'goods:list')).match()
        """)
public class StorageProductSkuController {

    private final SkuService skuService;
    private final SpecSkuService specSkuService;
    private final SkuStockService skuStockService;


    /**
     * 更新商品规格与sku信息
     *
     * @param productId 商品id
     * @param specSku   商品规格与sku信息
     */
    @Idem
    @Log("新增/更新商品规格与sku信息")
    @PostMapping("/specSku")
    public Result<Void> saveOrUpdateSpecSku(@PathVariable Long productId, @RequestBody @Valid StorageSpecSkuDTO specSku) {
        specSku.setProductId(productId);
        specSkuService.saveOrUpdateSpecSku(specSku);
        return Result.ok();
    }

    /**
     * 设置商品仓储信息
     *
     * @param productId 商品id
     * @param limits    商品库存信息
     */
    @PostMapping("/limit")
    @Log("设置商品限购")
    public Result<Void> setProductStorage(@PathVariable Long productId, @RequestBody @NotNull @Size(min = 1) List<ProductSkuLimitDTO> limits) {
        skuService.setProductLimit(productId, limits);
        return Result.ok();
    }

    /**
     * 更新商品库存
     *
     * @param productId 商品id
     * @param skuStock  sku调整列表
     */
    @Idem(value = 500)
    @Log("商户 增加/减少商品库存")
    @PostMapping("/stock")
    public Result<Void> updateSkuStock(@PathVariable Long productId, @RequestBody @Valid List<ProductSkuStockDTO> skuStock) {
        skuService.updateSkuStock(productId, skuStock);
        return Result.ok();
    }

    /**
     * 更新商品价格
     *
     * @param productId       商品id
     * @param productSkuPrice 商品sku价格
     * @return Result.ok()
     */
    @Idem(value = 500)
    @Log("商户 更改商品价格")
    @PostMapping("/price")
    public Result<Void> updateSkuPrice(@PathVariable Long productId, @RequestBody @Valid List<ProductSkuPriceDTO> productSkuPrice) {
        skuService.updateSkuPrice(productId, productSkuPrice);
        return Result.ok();
    }


    /**
     * 根据商品id查询商品库存信息
     *
     * @param productId 商品id
     * @return 库存列表
     */
    @Log("根据商品id查询商品库存信息")
    @GetMapping
    public Result<List<StorageSku>> getSkuStocksByProductId(@PathVariable Long productId) {
        Long shopId = ISecurity.userMust().getShopId();
        ActivityShopProductKey key = new ActivityShopProductKey();
        key.setProductId(productId).setShopId(shopId).setActivityType(OrderType.COMMON).setActivityId(0L);
        return Result.ok(
                skuStockService.productSkuStockBatch(
                        Set.of(key)
                ).values().stream().findFirst().orElse(null)
        );
    }

}
