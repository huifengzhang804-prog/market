package com.medusa.gruul.search.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.search.service.model.dto.SearchBrandQueryDTO;
import com.medusa.gruul.search.service.model.vo.SearchBrandInfoVO;
import com.medusa.gruul.search.service.model.vo.SearchBrandInitialVO;
import com.medusa.gruul.search.service.model.vo.SearchBrandVO;
import com.medusa.gruul.search.service.mp.entity.SearchBrand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 品牌 Mapper 接口
 *
 * @author WuDi
 * @since 2023-02-02
 */
public interface SearchBrandMapper extends BaseMapper<SearchBrand> {

    /**
     * 分页查询品牌
     * @param searchBrandQuery 查询参数
     * @return 分页品牌
     */
    IPage<SearchBrandVO> brandPage(@Param("searchBrandQuery") SearchBrandQueryDTO searchBrandQuery);
    /**
     * 分页查询品牌基本信息
     * @param searchBrandQuery 品牌基本信息
     * @return 品牌分页基本信息
     */
    IPage<SearchBrandInfoVO> brandInfoPage(@Param("searchBrandQuery") SearchBrandQueryDTO searchBrandQuery);
    /**
     * 根据品牌id查询品牌和首字母
     *
     * @param brandIds         品牌集合
     * @param parentCategoryId 类目id/三级分类id
     * @return 品牌名称首字母信息
     */
    List<SearchBrandInitialVO> getBrandInitialList(@Param("brandIds") List<Long> brandIds, @Param("parentCategoryId") Long parentCategoryId);

}
