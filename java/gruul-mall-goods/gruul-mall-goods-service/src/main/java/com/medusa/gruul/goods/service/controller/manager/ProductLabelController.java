package com.medusa.gruul.goods.service.controller.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.goods.api.entity.ProductLabel;
import com.medusa.gruul.goods.service.model.dto.ProductLabelDTO;
import com.medusa.gruul.goods.service.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品标签分类 前端控制器
 *
 * @author wufuzhong
 * @since 2023-12-02
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("""
                @S.matcher()
                .any(@S.ROLES,@S.R.SUPER_ADMIN,@S.R.SUPER_CUSTOM_ADMIN,@S.R.ADMIN,@S.R.CUSTOM_ADMIN).match()
        """)
@RequestMapping("/goods/product/label")
public class ProductLabelController {

    private final LabelService labelService;

    /**
     * 分页查询商品标签
     *
     * @param page 分页查询信息
     * @return 分页查询结果
     */
    @GetMapping
    @Log("分页查询商品标签")
    public Result<IPage<ProductLabel>> pageProductLabel(Page<Void> page) {
        return Result.ok(labelService.pageProductLabel(page));
    }

    @GetMapping("/labels")
    @Log("查询商品所有标签")
    public Result<List<ProductLabel>> listLabels() {
        return Result.ok(labelService.listLabels());
    }


    /**
     * 商品标签新增
     *
     * @param dto 商品标签dto
     */
    @Idem(500)
    @PostMapping
    @Log("新增商品标签")
    public Result<Void> newProductLabel(@RequestBody ProductLabelDTO dto) {
        labelService.newProductLabel(dto);
        return Result.ok();
    }

    /**
     * 编辑商品标签
     *
     * @param id  商品标签id
     * @param dto 商品标签dto
     */
    @Idem(500)
    @Log("编辑商品标签")
    @PutMapping("/{id}")
    public Result<Void> editProductLabel(@PathVariable("id") Long id, @RequestBody ProductLabelDTO dto) {
        labelService.editProductLabel(id, dto);
        return Result.ok();
    }

    /**
     * 删除商品标签
     *
     * @param id 商品标签id
     */
    @Log("删除商品标签")
    @DeleteMapping("/{id}")
    public Result<Void> deleteProductLabel(@PathVariable("id") Long id) {
        labelService.deleteProductLabel(id);
        return Result.ok();
    }

    /**
     * 根据店铺类型，查询商品标签
     *
     * @param shopType 店铺类型
     * @return 商品标签
     */
    @GetMapping("/searchByShopType/{shopType}")
    @PreAuthorize("permitAll()")
    public Result<List<ProductLabel>> searchProductLabelByShopType(@PathVariable ShopType shopType) {
        return Result.ok(labelService.searchProductLabelByShopType(shopType));
    }
}
