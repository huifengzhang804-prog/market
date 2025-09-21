package com.medusa.gruul.search.service.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.search.api.enums.BrandStatus;
import com.medusa.gruul.search.service.model.dto.SearchBrandDTO;
import com.medusa.gruul.search.service.model.dto.SearchBrandQueryDTO;
import com.medusa.gruul.search.service.model.vo.SearchBrandInfoVO;
import com.medusa.gruul.search.service.model.vo.SearchBrandVO;
import com.medusa.gruul.search.service.mp.entity.SearchBrand;
import com.medusa.gruul.search.service.mp.entity.SearchBrandFollow;
import com.medusa.gruul.search.service.mp.service.ISearchBrandFollowService;
import com.medusa.gruul.search.service.mp.service.ISearchBrandService;
import com.medusa.gruul.search.service.service.BrandManageService;
import com.medusa.gruul.search.service.service.EsProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandManageServiceImpl implements BrandManageService {

    private final ISearchBrandService brandService;
    private final EsProductService esProductService;
    private final ISearchBrandFollowService brandFollowService;

    /**
     * 新增品牌
     *
     * @param searchBrand 品牌
     */
    @Override
    public void addBrand(SearchBrandDTO searchBrand) {
        SearchBrand brand = searchBrand.saveBrand(Boolean.FALSE);
        boolean save = brandService.save(brand);
        if (!save) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "品牌添加失败");
        }
    }


    /**
     * 新增品牌
     *
     * @param searchBrand 品牌
     */
    @Override
    public void updateBrand(SearchBrandDTO searchBrand) {
        SearchBrand brand = searchBrand.saveBrand(Boolean.TRUE);
        boolean update = brandService.updateById(brand);
        if (!update) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED_CODE, "品牌修改失败");
        }
    }

    /**
     * 查询品牌
     *
     * @param brandId 品牌id
     * @return 品牌
     */
    @Override
    public SearchBrandVO getBrandById(Long brandId) {
        SearchBrand brand = brandService.lambdaQuery()
                .eq(SearchBrand::getId, brandId)
                .one();
        if (brand == null) {
            return null;
        }
        return new SearchBrandVO()
                .setBrandName(brand.getBrandName())
                .setBrandDesc(brand.getBrandDesc())
                .setBrandLogo(brand.getBrandLogo())
                .setSearchInitials(brand.getSearchInitials())
                .setParentCategoryId(brand.getParentCategoryId())
                .setStatus(brand.getStatus())
                .setFollowers(brand.getFollowers())
                .setSort(brand.getSort())
                .setId(brand.getId());
    }

    /**
     * 分页查询品牌
     *
     * @param searchBrandQuery 查询参数
     * @return 分页品牌
     */
    @Override
    public IPage<SearchBrandVO> brandPage(SearchBrandQueryDTO searchBrandQuery) {
        return brandService.brandPage(searchBrandQuery);
    }

    /**
     * 分页查询品牌基本信息
     *
     * @param searchBrandQuery 品牌基本信息
     * @return 品牌分页基本信息
     */
    @Override
    public IPage<SearchBrandInfoVO> brandInfoPage(SearchBrandQueryDTO searchBrandQuery) {
        return brandService.brandInfoPage(searchBrandQuery);
    }


    /**
     * 删除品牌
     *
     * @param brandId 品牌id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBrandById(Long brandId) {
        brandService.lambdaUpdate()
                .eq(SearchBrand::getId, brandId)
                .remove();
        brandFollowService.lambdaUpdate()
                .eq(SearchBrandFollow::getBrandId, brandId)
                .remove();
        esProductService.brandDelete(brandId);

    }

    /**
     * 品牌上下架
     *
     * @param brandId 品牌id
     * @param status  品牌状态
     */
    @Override
    public void updateBrandStatus(Long brandId, BrandStatus status) {
        boolean update = brandService.lambdaUpdate()
                .set(SearchBrand::getStatus, status)
                .eq(SearchBrand::getId, brandId)
                .update();
        if (!update) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED_CODE, "品牌状态更新成功");
        }
        esProductService.brandStatusUpdate(brandId, status);

    }


}
