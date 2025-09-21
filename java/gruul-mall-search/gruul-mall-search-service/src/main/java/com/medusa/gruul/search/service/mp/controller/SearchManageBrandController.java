package com.medusa.gruul.search.service.mp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.web.valid.group.Update;
import com.medusa.gruul.search.api.enums.BrandStatus;
import com.medusa.gruul.search.service.model.dto.SearchBrandDTO;
import com.medusa.gruul.search.service.model.dto.SearchBrandQueryDTO;
import com.medusa.gruul.search.service.model.vo.SearchBrandInfoVO;
import com.medusa.gruul.search.service.model.vo.SearchBrandVO;
import com.medusa.gruul.search.service.service.BrandManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 平台管理
 *
 * @author wudi
 * date 2023-02-18
 */
@RestController
@RequestMapping("/search/brand")
@PreAuthorize("@S.platformPerm('brandManage')")
@RequiredArgsConstructor
public class SearchManageBrandController {

    private final BrandManageService brandManageService;


    /**
     * 新增品牌
     *
     * @param searchBrandDTO 品牌
     */
    @Idem(500)
    @Log("新增品牌")
    @PostMapping
    public Result<Void> addBrand(@RequestBody @Validated SearchBrandDTO searchBrandDTO) {
        brandManageService.addBrand(searchBrandDTO);
        return Result.ok();
    }

    /**
     * 修改品牌
     *
     * @param searchBrandDTO 品牌
     */
    @Idem(500)
    @Log("修改品牌")
    @PutMapping
    public Result<Void> updateBrand(@RequestBody @Validated(Update.class) SearchBrandDTO searchBrandDTO) {
        brandManageService.updateBrand(searchBrandDTO);
        return Result.ok();
    }

    /**
     * 查询品牌
     *
     * @param brandId 品牌id
     * @return 品牌
     */
    @Log("查询品牌")
    @GetMapping("/{brandId}")
    @PreAuthorize("permitAll()")
    public Result<SearchBrandVO> brand(@PathVariable Long brandId) {
        return Result.ok(
                brandManageService.getBrandById(brandId)
        );
    }

    /**
     * 删除品牌
     *
     * @param brandId 品牌id
     */
    @Log("删除品牌")
    @DeleteMapping("/{brandId}")
    public Result<Void> deleteBrandById(@PathVariable Long brandId) {
        brandManageService.deleteBrandById(brandId);
        return Result.ok();
    }

    /**
     * 分页查询品牌
     *
     * @param searchBrandQuery 查询参数
     * @return 品牌分页
     */
    @Log("分页查询品牌")
    @GetMapping
    @PreAuthorize("permitAll()")
    public Result<IPage<SearchBrandVO>> brandPage(SearchBrandQueryDTO searchBrandQuery) {
        return Result.ok(
                brandManageService.brandPage(searchBrandQuery)
        );
    }

    /**
     * 分页查询品牌基本信息
     *
     * @param searchBrandQuery 品牌基本信息
     * @return 品牌分页基本信息
     */
    @Log("分页查询品牌基本信息")
    @PreAuthorize("permitAll()")
    @GetMapping("/brandInfo")
    public Result<IPage<SearchBrandInfoVO>> brandInfoPage(SearchBrandQueryDTO searchBrandQuery) {
        return Result.ok(
                brandManageService.brandInfoPage(searchBrandQuery)
        );
    }


    /**
     * 品牌上下架
     *
     * @param brandId 品牌id
     * @param status  品牌状态
     */
    @Log("品牌上下架")
    @PutMapping("/updateStatus/{brandId}/{status}")
    public Result<Void> updateBrandStatus(@PathVariable Long brandId,
                                          @PathVariable BrandStatus status) {
        brandManageService.updateBrandStatus(brandId, status);
        return Result.ok();
    }

}
