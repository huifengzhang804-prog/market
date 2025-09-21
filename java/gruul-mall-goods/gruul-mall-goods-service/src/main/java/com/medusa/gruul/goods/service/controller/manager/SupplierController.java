package com.medusa.gruul.goods.service.controller.manager;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.entity.Supplier;
import com.medusa.gruul.goods.api.model.dto.SupplierDTO;
import com.medusa.gruul.goods.service.model.param.SupplierParam;
import com.medusa.gruul.goods.service.model.param.SupplierProductParam;
import com.medusa.gruul.goods.service.model.vo.SupplierVO;
import com.medusa.gruul.goods.service.mp.service.ISupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 *
 * 供应商 前端控制器
 *
 * @author xiaoq
 * @since 2022-03-04
 */
@RestController
@RequestMapping("manager/supplier")
@PreAuthorize("@S.shopPerm('goods:list','goods:supplier')")
@RequiredArgsConstructor
public class SupplierController {

    private final ISupplierService supplierService;


    /**
     * 供应商删除
     *
     * @param ids 供应商ids
     */
    @Log("供应商删除")

    @DeleteMapping("/delete/{ids}")
    public Result<Void> deleteSupplierList(@PathVariable(name = "ids") Long[] ids) {
        supplierService.deleteSupplierList(ids);
        return Result.ok();
    }

    /**
     * 供应商新增
     *
     * @param  supplierDto 新增或修改供应商DTO
     */
    @Idem(1000)
    @Log("供应商新增")
    @PostMapping("/save")
    public Result<Void> addSupplier(@RequestBody @Validated SupplierDTO supplierDto) {
        supplierService.addSupplier(supplierDto);
        return Result.ok();
    }

    /**
     * 供应商修改
     *
     * @param supplierDto 新增或修改供应商DTO
     */
    @Idem(1000)
    @Log("供应商修改")
    @PutMapping("/update")
    public Result<Void> updateSupplier(@RequestBody SupplierDTO supplierDto) {
        supplierService.updateSupplier(supplierDto);
        return Result.ok();
    }

    /**
     * 供应商列表
     *
     * @param  supplierParam 供应商查询param
     * @return IPage<供应商VO>
     */
    @Log("供应商列表")
    @GetMapping("/list")
    public Result<IPage<SupplierVO>> getSupplierList(SupplierParam supplierParam) {
        IPage<SupplierVO> supplierVos = supplierService.getSupplierList(supplierParam);
        return Result.ok(supplierVos);
    }

    /**
     * 供应商审核
     *
     * @param supplierDto 供应商信息dto
     * ------- 接口预留
     * ------- 接口预留
     */
    @Log("供应商审核")
    @PutMapping("/check")
    public Result<Void> checkSupplier(@RequestBody SupplierDTO supplierDto) {
        supplierService.checkSupplier(supplierDto);
        return Result.ok();
    }

    /**
     * 供应商商品列表
     */
    @Log("供应商商品列表")
    @GetMapping("/product")
    public Result<IPage<Product>> getSupplierProductList(SupplierProductParam supplierProductParam) {
        IPage<Product> supplierProductList = supplierService.getSupplierProductList(supplierProductParam);
        return Result.ok(supplierProductList);
    }

    /**
     * 查询供应商
     * @param providerId 供应商id
     */
    @Log("查询供应商")
    @GetMapping("{providerId}")
    public Result<Supplier> getSupplierById(@PathVariable("providerId") Long providerId) {
        return Result.ok(supplierService.getById(providerId));
    }
}
