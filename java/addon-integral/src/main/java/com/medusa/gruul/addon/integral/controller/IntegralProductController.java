package com.medusa.gruul.addon.integral.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.integral.model.dto.IntegralProductDTO;
import com.medusa.gruul.addon.integral.model.dto.IntegralProductFixDTO;
import com.medusa.gruul.addon.integral.model.enums.IntegralProductStatus;
import com.medusa.gruul.addon.integral.model.param.IntegralProductParam;
import com.medusa.gruul.addon.integral.model.vo.IntegralProductListVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralProduct;
import com.medusa.gruul.addon.integral.service.CommonIntegralProductService;
import com.medusa.gruul.addon.integral.service.ManageIntegralProductService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * 积分商品控制层
 *
 * @author xiaoq
 * Description 积分商品控制层
 * date 2023-01-31 15:49
 */
@RestController
@RequestMapping("/integral/product")
@PreAuthorize("@S.platformPerm('fatetrue:integral')")
@RequiredArgsConstructor
public class IntegralProductController {


    private final ManageIntegralProductService manageIntegralProductService;

    private final CommonIntegralProductService commonIntegralProductService;

    /**
     * 积分商品发布
     *
     * @param integralProduct 积分商品DTO
     * @return Result<Void>
     */
    @Log("积分商品发布")
    @Idem(1000)
    @PostMapping("/issue")
    public Result<Void> issueIntegralProduct(@RequestBody @Valid IntegralProductDTO integralProduct) {
        manageIntegralProductService.issueIntegralProduct(integralProduct);
        return Result.ok();
    }


    /**
     * 积分商品信息删除
     *
     * @param ids 积分商品ids
     * @return Result.ok()
     */
    @Log("积分商品信息删除")
    @DeleteMapping("/delete/{ids}")
    public Result<Void> delIntegralProduct(@PathVariable(name = "ids") @NonNull Set<Long> ids) {
        manageIntegralProductService.delIntegralProduct(ids);
        return Result.ok();
    }


    /**
     * 积分商品修改
     *
     * @param integralProduct 积分商品DTO
     * @return Result.ok()
     */
    @Log("积分商品信息修改")
    @Idem(1000)
    @PutMapping("/update")
    public Result<Void> updateIntegralProduct(@RequestBody @Valid IntegralProductDTO integralProduct) {
        manageIntegralProductService.updateIntegralProduct(integralProduct);
        return Result.ok();
    }

    /**
     * 积分商品信息增量修改
     *
     * @param integralProductFix 修改内容
     */
    @Log("积分商品信息增量修改")
    @PutMapping("/update/increment")
    public Result<Void> updateProductIncrement(@Valid @RequestBody IntegralProductFixDTO integralProductFix) {
        manageIntegralProductService.updateProductIncrement(integralProductFix);
        return Result.ok();
    }


    /**
     * 积分商品列表
     *
     * @param integralProductParam 积分商品查询参数
     * @return Result<IPage < 积分商品vo>>
     */
    @Log("积分商品列表")
    @GetMapping("/list")
    @PreAuthorize("permitAll()")
    public Result<IPage<IntegralProductListVO>> getIntegralProductList(IntegralProductParam integralProductParam) {
        ISecurity.match().ifUser(secureUser -> integralProductParam.setIsConsumer(Boolean.TRUE));
        return Result.ok(commonIntegralProductService.getIntegralProductList(integralProductParam));
    }

    /**
     * 积分商品上下架
     *
     * @param ids    积分商品ids
     * @param status 积分商品修改状态值
     * @return Result.ok()
     */
    @Log("积分商品上下架")
    @Idem(500)
    @PutMapping("/updateStatus/{status}")
    public Result<Void> updateIntegralProductStatus(@RequestBody Set<Long> ids, @PathVariable("status") IntegralProductStatus status) {
        manageIntegralProductService.updateIntegralProductStatus(ids, status);
        return Result.ok();
    }


    /**
     * 积分商品详细信息
     *
     * @param id 积分商品id
     * @return Result.ok(积分商品详细信息)
     */
    @Log("积分商品信息详情")
    @GetMapping("/info")
    @PreAuthorize("permitAll()")
    public Result<IntegralProduct> getIntegralProductInfo(Long id) {
        return Result.ok(commonIntegralProductService.getIntegralProductInfo(id));
    }

}
