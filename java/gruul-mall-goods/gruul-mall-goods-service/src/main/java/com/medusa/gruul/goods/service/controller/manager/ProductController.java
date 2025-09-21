package com.medusa.gruul.goods.service.controller.manager;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.web.parameter.enums.BodyParam;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductDTO;
import com.medusa.gruul.goods.api.model.dto.ProductStatusChangeDTO;
import com.medusa.gruul.goods.api.model.enums.ProductAuditType;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.param.*;
import com.medusa.gruul.goods.api.model.vo.AuditProductVO;
import com.medusa.gruul.goods.api.model.vo.LookProductVO;
import com.medusa.gruul.goods.api.model.vo.ProductStockVO;
import com.medusa.gruul.goods.api.model.vo.ProductVO;
import com.medusa.gruul.goods.service.model.dto.ConsignmentProductDTO;
import com.medusa.gruul.goods.service.model.param.PurchaseProductParam;
import com.medusa.gruul.goods.service.model.vo.SupplierIssueProductListVO;
import com.medusa.gruul.goods.service.mp.service.IProductService;
import com.medusa.gruul.goods.service.service.ProductAuditService;
import com.medusa.gruul.storage.api.vo.ProductSkusVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;


/**
 * 商品信息表 前端控制器
 *
 * @author xiaoq
 * @since 2022-03-04
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/manager/product")
@PreAuthorize("@S.shopPerm('goods:list')")
public class ProductController {

    private final IProductService productService;

    private final ProductAuditService productAuditService;

    /**
     * 商品发布(新增)
     *
     * @param productDto 商品信息dto
     */
    @Log("商品发布")
    @Idem(1000)
    @PostMapping("/issue")
    public Result<String> issueProduct(@RequestBody @Validated ProductDTO productDto) {
        // 校验规格名
        productDto.checkSpecGroups();
        Product product = productService.issueProduct(productDto);
        String content = ProductStatus.UNDER_REVIEW.equals(product.getStatus()) ? "商品正在审核中" : "";
        return Result.ok(content);
    }


    /**
     * 商品信息删除
     *
     * @param ids 商品id
     */
    @Log("商品信息删除")
    @DeleteMapping("/delete/{ids}")
    public Result<Void> deleteProductList(@PathVariable(name = "ids") Set<Long> ids) {
        productService.deleteProductList(ids);
        return Result.ok();
    }


    /**
     * 商品信息修改
     *
     * @param productDto 商品信息DTO
     */
    @Log("商品信息修改")
    @Idem(1000)
    @PutMapping("/update")
    public Result<Void> updateProduct(@RequestBody @Validated ProductDTO productDto) {
        productService.updateProduct(productDto);
        return Result.ok();
    }


    /**
     * 商品信息列表
     *
     * @param productParam 商品查询param
     * @return IPage<商品信息VO>
     */
    @Log("商品信息列表")
    @GetMapping("/list")
    @PreAuthorize("@S.anyPerm('commodity','goods:list')")
    public Result<IPage<ProductVO>> getProductList(ProductParam productParam) {
        return Result.ok(productService.getProductList(productParam));
    }

    /**
     * 查询指定类型的商品数量
     *
     * @param productParam
     * @return
     */
    @Log("查询指定类型的商品数量")
    @GetMapping("/status/count")
    @PreAuthorize("@S.anyPerm('commodity','goods:list')")
    public Result<Long> getProductCount(ProductParamNoPage productParam) {
        ISecurity.match().when(security -> productParam.setShopId(security.getShopId()),
                Roles.ADMIN, Roles.CUSTOM_ADMIN);
        return Result.ok(productService.getProductCount(productParam));
    }

    /**
     * 查询违规下架商品数量
     *
     * @param productParam
     * @return 违规商品数量
     */
    @Log("查询违规下架商品数量")
    @GetMapping("/illegalCount")
    @PreAuthorize("@S.platformPerm('commodity','goods:list')")
    public Result<Integer> illegalProductCount(@Validated PlatformProductParamNoPage productParam) {
        ISecurity.match().when(security -> productParam.setShopId(security.getShopId()),
                Roles.ADMIN, Roles.CUSTOM_ADMIN);
        //平台违规下架
        productParam.setStatus(ProductStatus.PLATFORM_SELL_OFF);
        return Result.ok(productService.illegalProductCount(productParam));
    }

    /**
     * 查询待审核商品数量
     *
     * @param productParam
     * @return
     */
    @Log("待审核商品数量")
    @PostMapping("/auditingCount")
    @PreAuthorize("@S.anyPerm('commodity','goods:list')")
    public Result<Integer> auditingCount(@Validated AuditProductParamNoPage productParam) {
        ISecurity.match().when(security -> productParam.setShopId(security.getShopId()),
                Roles.ADMIN, Roles.CUSTOM_ADMIN);

        return Result.ok(productService.auditingCount(productParam));
    }


    /**
     * 获取单个商品基础信息
     *
     * @param shopId 店铺id
     * @param id     商品id
     * @return 商品信息Vo
     */
    @Log("单个商品信息获取By id")
    @GetMapping("/get/{shopId}/{id}")
    public Result<ProductVO> getProductById(@PathVariable("shopId") Long shopId, @PathVariable("id") Long id) {
        ProductVO productVo = productService.getProductById(id, shopId);
        return Result.ok(productVo);
    }


    /**
     * 商品上下架
     *
     * @param productStatusChange 商品状态更改信息
     * @param status              商品状态值
     */
    @Log("商品上下架 违规下架")
    @Idem(500)
    @PutMapping("/updateStatus/{status}")
    @PreAuthorize("@S.anyPerm('commodity','goods:list')")
    public Result<Void> updateProductStatus(@RequestBody @Valid ProductStatusChangeDTO productStatusChange,
                                            @PathVariable("status") ProductStatus status) {
        boolean isPlatform = ISecurity.anyRole(Roles.SUPER_ADMIN, Roles.SUPER_CUSTOM_ADMIN);
        productService.updateProductStatus(isPlatform, productStatusChange, status);
        return Result.ok();
    }

    /**
     * 违规下架商品恢复销售 改为已下架的状态
     *
     * @return
     */
    @PostMapping("/restoreSale")
    @Log("违规下架商品恢复销售")
    @Idem(500)
    @PreAuthorize("@S.platformPerm('commodity','goods:list')")
    public Result<Void> restoreSale(@RequestBody @Valid ShopProductKey shopProductKey) {
        productService.restoreSale(shopProductKey);
        return Result.ok();
    }


    /**
     * 平台获取商品数量/违规商品数量
     *
     * @return <map <ProductStatus,数量>>
     */
    @Log("平台获取商品数量/违规商品数量")
    @GetMapping("/quantity")
    @PreAuthorize("""
            		@S.matcher().any(@S.ROLES,@S.PLATFORM_ADMIN).
            		or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'overview'))
            		.match()
            """)
    public Result<Map<ProductStatus, Long>> getGoodsQuantity() {
        return Result.ok(productService.getGoodsQuantity());
    }


    /**
     * 获取今日新增商品数量
     *
     * @return 今日新增商品数量
     */
    @Log("获取今日新增商品数量")
    @GetMapping("/today/quantity")
    @PreAuthorize("""
            		@S.matcher().any(@S.ROLES,@S.PLATFORM_ADMIN,@S.R.SUPPLIER_ADMIN,@S.SHOP_ADMIN).
            		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'overview')).
            		or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'overview'))
            		.match()
            """)
    public Result<Long> getTodayAddGoodsQuantity() {
        return Result.ok(productService.getTodayAddGoodsQuantity());
    }

    /**
     * 分页获取商品和规格信息
     *
     * @param productParam 商品查询参数
     * @return 商品规格信息
     */
    @Log("分页获取商品和规格信息")
    @GetMapping("/getProductSkus")
    @PreAuthorize("@S.anyPerm('commodity','goods:list')")
    public Result<IPage<ProductSkusVO>> getProductSkus(ProductParam productParam) {
        return Result.ok(productService.getProductSkus(productParam));
    }


    /**
     * 商品名称修改
     *
     * @param id   商品id
     * @param name 商品name
     * @return void
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("@S.anyPerm('commodity','goods:list')")
    public Result<Void> updateProductName(@PathVariable("id") Long id, @Size(max = 35, message = "商品名称过长") @BodyParam String name) {
        productService.updateProductName(id, name);
        return Result.ok();
    }

    @Log("修改商品标签")
    @PutMapping({"updateProductLabel/{id}/{labelId}", "updateProductLabel/{id}"})
    @PreAuthorize("@S.shopPerm('commodity','goods:list')")
    public Result<Boolean> updateProductLabel(@PathVariable("id") Long id,
                                              @PathVariable(value = "labelId", required = false) Long labelId) {
        productService.updateProductLabel(id, labelId);
        return Result.ok(Boolean.TRUE);
    }


    /**
     * 获取商品库存基础信息
     *
     * @param param 检索条件
     * @return IPage<ProductSkusVO>
     */
    @Log("获取商品库存基础信息")
    @GetMapping("/getProductStockBaseInfo")
    public Result<IPage<ProductStockVO>> getProductStockBaseInfo(ProductStockParam param) {
        if (param.getProductIds() != null) {
            param.setProductIdList(param.getProductIds().split(","));
        }

        return Result.ok(productService.getProductStockBaseInfo(param));
    }


    /**
     * 获取采购已发布的商品信息
     *
     * @param param 查询parmam
     * @return IPage<SupplierIssueProductListVO>
     */
    @Log("获取采购已发布的商品信息")
    @GetMapping("/purchase/issue/products")
    public Result<IPage<SupplierIssueProductListVO>> getPurchaseIssueProducts(PurchaseProductParam param) {
        return Result.ok(productService.getPurchaseIssueProducts(param));
    }


    /**
     * 采购已发布商品修改状态
     *
     * @param id 商品id
     * @return Result<Void>
     */
    @Idem(1000)
    @Log("采购已发布商品修改状态")
    @PutMapping("purchase/issue/product/updateStatus/{id}")
    public Result<Void> purchaseIssueProductUpdateStatus(@PathVariable("id") @NotNull Long id) {
        productService.purchaseIssueProductUpdateStatus(id);
        return Result.ok();
    }


    /**
     * 获取代销商品详情
     *
     * @param id 商品id
     * @return 代销商品详情
     */
    @Log("获取代销商品详情")
    @GetMapping("consignment/{id}")
    public Result<ProductVO> consignmentProductInfo(@PathVariable("id") @NotNull Long id) {
        return Result.ok(productService.getConsignmentProductInfo(id));
    }

    /**
     * 代销商品修改
     *
     * @param consignmentProduct 代销商品修改DTO
     * @return Result<Void>
     */
    @Log("代销商品修改")
    @PostMapping("consignment/update")
    public Result<Void> consignmentProductUpdate(@RequestBody @Validated ConsignmentProductDTO consignmentProduct) {
        productService.consignmentProductUpdate(consignmentProduct);
        return Result.ok();
    }


    /**
     * 商品查看
     *
     * @param id 商品id
     * @return 商品查看信息
     */
    @Log("B端商品查看")
    @GetMapping("show/{id}")
    @PreAuthorize("@S.anyPerm('commodity','goods:list')")
    public Result<LookProductVO> getLookProductInfo(@PathVariable("id") @NotNull Long id, Long shopId) {
        if (shopId == null) {
            shopId = ISecurity.userMust().getShopId();
        }
        return Result.ok(productService.getLookProductInfo(id, shopId));
    }

    /**
     * 审核的商品数据
     *
     * @return AuditProductVO
     */
    @Log("审核商品数据")
    @GetMapping("audit")
    public Result<IPage<AuditProductVO>> getAuditProductList(AuditProductParam auditProductParam) {
        return Result.ok(productService.getAuditProductList(auditProductParam));
    }


    /**
     * 以拒绝的商品再次提交审核
     *
     * @param id 商品id
     * @return Void
     */
    @Log("已拒绝的商品再次提交")
    @PutMapping("audit/product/submit/{id}")
    public Result<Void> auditProductSubmit(@PathVariable("id") @NotNull Long id) {
        productService.auditProductSubmit(id);
        return Result.ok();
    }

    /**
     * 商品审核配置
     *
     * @return 审核类型
     */
    @Log("商品审核配置")
    @GetMapping("audit/setting")
    @PreAuthorize("""
            		@S.matcher().any(@S.ROLES,@S.PLATFORM_ADMIN).
            		or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'generalSet'))
            		.match()
            """)
    public Result<ProductAuditType> productAuditSetting() {
        return Result.ok(productAuditService.productAuditSetting());
    }


    /**
     * 商品审核配置修改
     *
     * @param productAuditType 商品审核配置类型
     * @return Void
     */
    @Idem(1000)
    @Log("商品审核配置修改")
    @PutMapping("audit/setting/{productAuditType}")
    @PreAuthorize("""
            		@S.matcher().any(@S.ROLES,@S.PLATFORM_ADMIN).
            		or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'generalSet'))
            		.match()
            """)
    public Result<Void> productAuditSetting(@PathVariable("productAuditType") @NotNull ProductAuditType productAuditType) {
        productAuditService.updateProductAuditSetting(productAuditType);
        return Result.ok();
    }

}