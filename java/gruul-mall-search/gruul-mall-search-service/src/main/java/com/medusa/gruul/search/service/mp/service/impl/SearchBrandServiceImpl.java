package com.medusa.gruul.search.service.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.search.service.model.dto.SearchBrandQueryDTO;
import com.medusa.gruul.search.service.model.vo.SearchBrandInfoVO;
import com.medusa.gruul.search.service.model.vo.SearchBrandInitialVO;
import com.medusa.gruul.search.service.model.vo.SearchBrandVO;
import com.medusa.gruul.search.service.mp.entity.SearchBrand;
import com.medusa.gruul.search.service.mp.mapper.SearchBrandMapper;
import com.medusa.gruul.search.service.mp.service.ISearchBrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 品牌 服务实现类
 *
 * @author WuDi
 * @since 2023-02-02
 */
@Service
public class SearchBrandServiceImpl extends ServiceImpl<SearchBrandMapper, SearchBrand> implements ISearchBrandService {

    /**
     * 分页查询品牌
     *
     * @param searchBrandQuery 查询参数
     * @return 分页品牌
     */
    @Override
    public IPage<SearchBrandVO> brandPage(SearchBrandQueryDTO searchBrandQuery) {
        return baseMapper.brandPage(searchBrandQuery);
    }

    /**
     * 分页查询品牌基本信息
     *
     * @param searchBrandQuery 品牌基本信息
     * @return 品牌分页基本信息
     */
    @Override
    public IPage<SearchBrandInfoVO> brandInfoPage(SearchBrandQueryDTO searchBrandQuery) {
        return baseMapper.brandInfoPage(searchBrandQuery);
    }

    /**
     * 根据品牌id查询品牌和首字母
     *
     * @param brandIds         品牌集合
     * @param parentCategoryId 类目id/三级分类id
     * @return 品牌名称首字母信息
     */
    @Override
    public List<SearchBrandInitialVO> getBrandInitialList(List<Long> brandIds, Long parentCategoryId) {
        return baseMapper.getBrandInitialList(brandIds,parentCategoryId);
    }


}
