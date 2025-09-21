package com.medusa.gruul.addon.platform.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.addon.platform.model.vo.CategoryVO;
import com.medusa.gruul.addon.platform.model.vo.SigningCategoryVO;
import com.medusa.gruul.addon.platform.mp.entity.PlatformShopSigningCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 平台店铺签约类目持久层
 *
 * @author xiaoq
 * @Description PlatformShopSigningCategoryMapper.java
 * @date 2023-05-15 10:10
 */
public interface PlatformShopSigningCategoryMapper extends BaseMapper<PlatformShopSigningCategory> {
    /**
     * 获取店铺签约类目信息
     *
     * @param shopId 店铺id
     * @param match 是否是平台请求
     * @return List<SigningCategoryVO>
     */
    List<SigningCategoryVO> querySigningCategoryListByShopId(@Param("shopId") Long shopId, @Param("match") boolean match);

    /**
     *  店铺端签约类目List--携带三级类目
     * @param shopId 店铺id
     * @return List<CategoryVO>
     */
    List<CategoryVO> queryChoosableCategoryInfo(@Param("shopId") Long shopId);
}
