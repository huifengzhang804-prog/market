package com.medusa.gruul.search.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.search.service.model.dto.SearchBrandQueryDTO;
import com.medusa.gruul.search.service.model.vo.SearchBrandInfoVO;
import com.medusa.gruul.search.service.model.vo.SearchBrandInitialVO;
import com.medusa.gruul.search.service.model.vo.SearchBrandVO;
import com.medusa.gruul.search.service.mp.entity.SearchBrand;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 品牌 服务类
 *
 * @author WuDi
 * @since 2023-02-02
 */
public interface ISearchBrandService extends IService<SearchBrand> {

    /**
     * 分页查询品牌
     *
     * @param searchBrandQuery 查询参数
     * @return 分页品牌
     */
    IPage<SearchBrandVO> brandPage(SearchBrandQueryDTO searchBrandQuery);

    /**
     * 分页查询品牌基本信息
     *
     * @param searchBrandQuery 品牌基本信息
     * @return 品牌分页基本信息
     */
    IPage<SearchBrandInfoVO> brandInfoPage(SearchBrandQueryDTO searchBrandQuery);

    /**
     * 根据品牌id查询品牌和首字母
     *
     * @param brandIds         品牌集合
     * @param parentCategoryId 类目id/三级分类id
     * @return 品牌名称首字母信息
     */
    List<SearchBrandInitialVO> getBrandInitialList(List<Long> brandIds, Long parentCategoryId);

}
