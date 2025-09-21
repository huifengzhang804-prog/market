package com.medusa.gruul.addon.platform.mp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.platform.model.vo.CategoryVO;
import com.medusa.gruul.addon.platform.model.vo.SigningCategoryVO;
import com.medusa.gruul.addon.platform.mp.entity.PlatformShopSigningCategory;
import com.medusa.gruul.addon.platform.mp.mapper.PlatformShopSigningCategoryMapper;
import com.medusa.gruul.addon.platform.mp.service.IPlatformShopSigningCategoryService;
import com.medusa.gruul.common.model.constant.CommonPool;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 平台店铺签约类目持久层
 *
 * @author xiaoq
 * @Description PlatformShopSigningCategoryServiceImpl.java
 * @date 2023-05-15 10:09
 */
@Service
public class PlatformShopSigningCategoryServiceImpl extends ServiceImpl<PlatformShopSigningCategoryMapper, PlatformShopSigningCategory> implements IPlatformShopSigningCategoryService {
    @Override
    public List<SigningCategoryVO> getSigningCategoryListByShopId(Long shopId,boolean match) {
        return this.baseMapper.querySigningCategoryListByShopId(shopId,match);
    }

    @Override
    public List<CategoryVO> getChoosableCategoryInfo(Long shopId) {
        return this.baseMapper.queryChoosableCategoryInfo(shopId);
    }

    /**
     * 查询没有设置自定义抵扣比例的类目信息
     * @param categoryId 二级分类id
     * @return 查询到的数据
     */
    @Override
    public List<PlatformShopSigningCategory> queryNonSigningCategory(Long categoryId) {
        List<PlatformShopSigningCategory> list = lambdaQuery().eq(
                PlatformShopSigningCategory::getCurrentCategoryId, categoryId)
            .isNull(PlatformShopSigningCategory::getCustomDeductionRatio)
            .eq(PlatformShopSigningCategory::getDeleted, CommonPool.NUMBER_ZERO).list();
        return list;
    }


}
