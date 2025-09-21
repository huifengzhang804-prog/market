package com.medusa.gruul.search.service.service;

import com.medusa.gruul.search.service.model.vo.SearchBrandDetailVO;
import com.medusa.gruul.search.service.model.vo.SearchBrandInitialVO;

import java.util.List;
import java.util.Map;

public interface BrandQueryService {
    /**
     * 查询品牌详情
     * @param brandId  品牌id
     * @return  品牌详情
     */
    SearchBrandDetailVO getBrandDetailById(Long brandId);

    /**
     * 根据品牌id查询品牌和首字母
     *
     * @param brandIds 品牌集合
     * @return 品牌名称首字母信息
     */
    Map<String, List<SearchBrandInitialVO>> getBrandInitialList(List<Long> brandIds);

    /**
     * 根据类目id/三级分类id查询品牌
     * @param parentCategoryId 类目id/三级分类id
     * @return 品牌和首字母
     */
    Map<String,List<SearchBrandInitialVO>> getBrandInitial(Long parentCategoryId);
}
