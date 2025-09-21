package com.medusa.gruul.search.service.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.search.service.model.dto.SearchBrandDTO;
import com.medusa.gruul.search.service.model.dto.SearchBrandQueryDTO;
import com.medusa.gruul.search.api.enums.BrandStatus;
import com.medusa.gruul.search.service.model.vo.SearchBrandInfoVO;
import com.medusa.gruul.search.service.model.vo.SearchBrandVO;

public interface BrandManageService {

    /**
     * 新增品牌
     * @param searchBrandDTO 品牌
     */
    void addBrand(SearchBrandDTO searchBrandDTO);
    /**
     * 修改品牌
     * @param searchBrandDTO 品牌
     */
    void updateBrand(SearchBrandDTO searchBrandDTO);

    /**
     * 查询品牌
     * @param brandId 品牌id
     * @return 品牌
     */
    SearchBrandVO getBrandById(Long brandId);
    /**
     * 删除品牌
     * @param brandId 品牌id
     */
    void deleteBrandById(Long brandId);
    /**
     * 品牌上下架
     * @param brandId  品牌id
     * @param status   品牌状态
     */
    void updateBrandStatus(Long brandId, BrandStatus status);
    /**
     * 分页查询品牌
     * @param searchBrandQuery 查询参数
     * @return 分页品牌
     */
     IPage<SearchBrandVO> brandPage(SearchBrandQueryDTO searchBrandQuery);
    /**
     * 分页查询品牌基本信息
     * @param searchBrandQuery 品牌基本信息
     * @return 品牌分页基本信息
     */
    IPage<SearchBrandInfoVO> brandInfoPage(SearchBrandQueryDTO searchBrandQuery);
}
