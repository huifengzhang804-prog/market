package com.medusa.gruul.addon.supplier.modules.goods.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.supplier.model.dto.ProductStatusChangeDTO;
import com.medusa.gruul.addon.supplier.model.param.SupplierProductParam;
import com.medusa.gruul.addon.supplier.model.param.SupplierProductParamNoPage;
import com.medusa.gruul.addon.supplier.model.param.SupplyListParam;
import com.medusa.gruul.addon.supplier.model.vo.SupplierProductListVO;
import com.medusa.gruul.addon.supplier.modules.goods.service.EditSupplierGoodsService;
import com.medusa.gruul.addon.supplier.modules.goods.service.QuerySupplierGoodsService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.web.parameter.enums.BodyParam;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.param.AuditProductParam;
import com.medusa.gruul.goods.api.model.vo.AuditProductVO;
import com.medusa.gruul.goods.api.model.vo.LookProductVO;
import com.medusa.gruul.goods.api.model.vo.ProductVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 供货商商品控制层
 *
 * @author xiaoq
 * @since 2023-07-17 09:42
 */
@Validated
@RestController
@RequestMapping("/supplier/manager/product")
@RequiredArgsConstructor
@PreAuthorize("@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN).match()")
public class SupplierGoodsController {
    private final EditSupplierGoodsService editSupplierGoodsService;
    private final QuerySupplierGoodsService querySupplierGoodsService;

    /**
     * 供应商商品发布
     *
     * @param supplierProduct 供应商商品信息
     * @return Result.ok()
     */
    @Log("供应商商品发布")
    @Idem(1000)
    @PostMapping("/issue")
    public Result<String> issueSupplierProduct(@RequestBody @Validated ProductDTO supplierProduct) {
        supplierProduct.checkSpecGroups();

        Product product = editSupplierGoodsService.issueSupplierProduct(supplierProduct);

        return Result.ok(ProductStatus.UNDER_REVIEW.equals(product.getStatus()) ? "商品正在审核中" : "");
    }


    /**
     * 供应商商品信息删除
     *
     * @param ids 商品id
     */
    @Log("供应商商品信息删除")
    @DeleteMapping("/delete/{ids}")
    public Result<Void> deleteSupplierProductList(@PathVariable(name = "ids") Set<Long> ids) {
        editSupplierGoodsService.deleteSupplierProductList(ids);
        return Result.ok();
    }


    /**
     * 供应商商品信息修改
     *
     * @param supplierProduct 供应商商品信息
     * @return Result.ok()
     */
    @Log("供应商商品信息修改")
    @Idem(1000)
    @PutMapping("/update")
    public Result<Void> updateSupplierProduct(@RequestBody @Validated ProductDTO supplierProduct) {
        editSupplierGoodsService.updateSupplierProduct(supplierProduct);
        return Result.ok();
    }


    /**
     * 供应商商品信息列表
     *
     * @param supplierProductParam 供应商商品查询param
     * @return IPage<商品信息VO>
     */
    @Log("供应商商品信息列表")
    @GetMapping("/list")
    @PreAuthorize("""
             @S.matcher().any(@S.ROLES,@S.R.SUPPLIER_CUSTOM_ADMIN,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN,@S.SHOP_ADMIN)
             .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).any(@S.PERMS,'commodity','goods:purchase'))
             .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'commodity','goods:purchase'))
             .match()
            """)
    public Result<IPage<SupplierProductListVO>> getSupplierProductList(SupplierProductParam supplierProductParam) {
        ISecurity.match().when(
                secureUser -> supplierProductParam.setShopId(secureUser.getShopId()),
                Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN
        );

        return Result.ok(querySupplierGoodsService.getSupplierProductList(supplierProductParam));
    }

    /**
     * 查询违规下架供应商商品数量
     *
     * @param supplierProductParam 查询参数
     * @return 数量
     */
    @Log("违规下架供应商商品数量")
    @GetMapping("/illegalCount")
    @PreAuthorize("""
             @S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN,@S.SHOP_ADMIN)
             .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).any(@S.PERMS,'commodity','goods:purchase'))
             .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'commodity','goods:purchase'))
             .match()
            """)
    public Result<Integer> illegalCount(SupplierProductParamNoPage supplierProductParam) {
        ISecurity.match().when(
                secureUser -> supplierProductParam.setShopId(secureUser.getShopId()),
                Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN
        );
        //平台违规下架
        supplierProductParam.setSupplierProductStatus(ProductStatus.PLATFORM_SELL_OFF);
        return Result.ok(querySupplierGoodsService.illegalCount(supplierProductParam));
    }


    /**
     * 获取单个供应商商品基础信息
     *
     * @param shopId 店铺id
     * @param id     商品id
     * @return 商品信息VO
     */
    @Log("单个商品信息获取By id")
    @GetMapping("/get/{shopId}/{id}")
    @PreAuthorize("""
             @S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN,@S.SHOP_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN)
             .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).any(@S.PERMS,'commodity','goods:purchase'))
             .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'commodity','goods:purchase'))
             .match()
            """)
    public Result<ProductVO> getSupplierProductById(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id) {
        return Result.ok(querySupplierGoodsService.getSupplierProductById(shopId, id));
    }

    /**
     * 供应商商品名称修改
     *
     * @param id   商品id
     * @param name 商品name
     * @return void
     */
    @PutMapping("/update/{id}")
    public Result<Void> updateSupplierProductName(@PathVariable("id") Long id, @Size(max = 35, message = "商品名称不能超过35个字") @NotBlank @BodyParam String name) {
        editSupplierGoodsService.updateSupplierProductName(id, name);
        return Result.ok();
    }


    /**
     * 供应商商品上下架
     *
     * @param productStatusChange 商品状态更改信息
     * @param status              商品状态值
     */
    @Log("商品上下架")
    @Idem(500)
    @PutMapping("/updateStatus/{status}")
    @PreAuthorize("""
            @S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).any(@S.PERMS,'commodity','goods:purchase')).match()
            """)
    public Result<Void> updateSupplierProductStatus(@RequestBody ProductStatusChangeDTO productStatusChange,
                                                    @PathVariable("status") ProductStatus status) {

        editSupplierGoodsService.updateSupplierProductStatus(
                ISecurity.anyRole(Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN),
                productStatusChange,
                status
        );
        return Result.ok();
    }

    /**
     * 供应商商品恢复销售
     *
     * @param key 商品 key
     * @return void
     */
    @Log("供应商商品恢复销售")
    @Idem(500)
    @PostMapping("/restoreSale")
    @PreAuthorize("@S.platformPerm('commodity','goods:purchase')")
    public Result<Void> restoreSaleSupplierProduct(@RequestBody @Valid ShopProductKey key) {
        editSupplierGoodsService.restoreSaleSupplierProduct(key);
        return Result.ok();
    }


    /**
     * 商家端获取供应商货源
     */
    @Log("商家端获取供应商货源")
    @GetMapping("/getSupplyList")
    @PreAuthorize("""
            @S.matcher().any(@S.ROLES,@S.SHOP_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'commodity','goods:purchase'))
            .match()
            """)
    public Result<IPage<SupplierProductListVO>> getSupplyListByPlatformCategory(SupplyListParam supplyListParam) {
        return Result.ok(
                querySupplierGoodsService.getSupplyListByPlatformCategory(supplyListParam)
        );
    }


    /**
     * 获取商品库存基础信息 包含以删除
     *
     * @param supplierProductParam 查询数据
     * @return IPage<SupplierProductListVO>
     */
    @Log("获取商品库存基础信息")
    @GetMapping("/productStock/list")
    public Result<IPage<SupplierProductListVO>> getProductStockBaseList(SupplierProductParam supplierProductParam) {
        if (supplierProductParam.getProductIds() != null) {
            supplierProductParam.setProductIdList(supplierProductParam.getProductIds().split(","));
        }
        ISecurity.match().when(
                secureUser -> supplierProductParam.setShopId(secureUser.getShopId()),
                Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN
        );
        return Result.ok(
                querySupplierGoodsService.getProductStockBaseList(supplierProductParam)
        );
    }


    /**
     * 商品查看
     *
     * @param id 商品id
     * @return 商品查看信息
     */
    @Log("供应商B端商品查看")
    @GetMapping("show/{id}")
    @PreAuthorize("""
            @S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN,@S.SHOP_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'commodity'))
            .match()
            """)
    public Result<LookProductVO> getLookProductInfo(@PathVariable("id") @NotNull Long id, Long shopId) {
        if (shopId == null) {
            shopId = ISecurity.userMust().getShopId();
        }
        return Result.ok(querySupplierGoodsService.getLookProductInfo(id, shopId));
    }


    /**
     * 供应商审核的商品数据
     *
     * @return AuditProductVO
     */
    @Log("供应商审核商品数据")
    @GetMapping("audit")
    @PreAuthorize("""
            @S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN,@S.PLATFORM_ADMIN,@S.SHOP_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'commodity'))
            .match()
            """)
    public Result<IPage<AuditProductVO>> getSupplierAuditProductList(AuditProductParam auditProductParam) {
        ISecurity.match()
                .ifAnySupplierAdmin(secureUser -> auditProductParam.setShopId(secureUser.getShopId()));
        return Result.ok(querySupplierGoodsService.getSupplierAuditProductList(auditProductParam));
    }


    /**
     * 已拒绝的商品再次提交审核
     *
     * @param id 商品id
     * @return Void
     */
    @Log("已拒绝的商品再次提交")
    @PutMapping("audit/product/submit/{id}")
    public Result<Void> auditProductSubmit(@PathVariable("id") @NotNull Long id) {
        editSupplierGoodsService.supplierAuditProductSubmit(id);
        return Result.ok();
    }

}
