package com.medusa.gruul.goods.service.controller.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesCreateDTO;
import com.medusa.gruul.goods.api.model.dto.ProductFeaturesModifyDTO;
import com.medusa.gruul.goods.api.model.enums.FeaturesType;
import com.medusa.gruul.goods.api.model.vo.ProductFeaturesVO;
import com.medusa.gruul.goods.service.model.param.ProductFeaturesParam;
import com.medusa.gruul.goods.service.service.ManagerProductFeatureService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品特性控制层
 *
 * @author xiaoq
 * @since 2023-06-15
 */
@RestController
@RequestMapping("manager/feature")
@PreAuthorize("""
        		@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN,@S.SHOP_ADMIN).
        		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'goods:list'))
        		.match()
        """)
@RequiredArgsConstructor
public class ProductFeaturesController {


    private final ManagerProductFeatureService managerProductFeatureService;


    /**
     * 产品 特性保存
     *
     * @param dto 商品特性值DTO
     * @return Result.ok()
     */
    @Idem(1000)
    @Log("产品 特性保存")
    @PostMapping("/save")
    public Result<Void> saveProductFeature(@RequestBody @Valid ProductFeaturesCreateDTO dto) {
        managerProductFeatureService.saveProductFeature(dto);
        return Result.ok();
    }


    /**
     * 产品 特性修改
     *
     * @param dto 商品特性值DTO
     * @return Result.ok()
     */
    @Idem(1000)
    @Log("产品 特性修改")
    @PostMapping("/update")
    public Result<Void> updateProductFeature(@RequestBody @Valid ProductFeaturesModifyDTO dto) {
        managerProductFeatureService.updateProductFeature(dto);
        return Result.ok();
    }


    /**
     * 产品 特性列表
     *
     * @return Result.ok()
     */
    @Log("产品 特性列表")
    @GetMapping("/list")
    public Result<IPage<ProductFeaturesVO>> getProductFeatureList(@Validated ProductFeaturesParam param) {
        return Result.ok(managerProductFeatureService.getProductFeatureList(param));
    }


    /**
     * 产品 特性删除
     *
     * @param ids          产品特性ids
     * @param featuresType 产品特性类型
     * @return Result.ok()
     */
    @Log("产品 特性删除")
    @PutMapping("/del")
    public Result<Void> delProductFeature(@RequestBody List<Long> ids, @NotNull FeaturesType featuresType) {
        managerProductFeatureService.delProductFeature(ids, featuresType);
        return Result.ok();
    }

}
