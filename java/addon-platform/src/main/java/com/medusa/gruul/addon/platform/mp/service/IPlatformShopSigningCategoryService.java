package com.medusa.gruul.addon.platform.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.platform.model.vo.CategoryVO;
import com.medusa.gruul.addon.platform.model.vo.SigningCategoryVO;
import com.medusa.gruul.addon.platform.mp.entity.PlatformShopSigningCategory;

import java.util.List;

/**
 * 平台店铺签约类目服务层
 *
 * @author xiaoq
 * @Description IPlatformShopSigningCategoryService.java
 * @date 2023-05-15 10:08
 */
public interface IPlatformShopSigningCategoryService extends IService<PlatformShopSigningCategory> {
    /**
     * 获取签约类目信息
     *
     * @param shopId 店铺id
     * @param match 是否是平台请求
     * @return List<签约类目VO></>
     */
    List<SigningCategoryVO> getSigningCategoryListByShopId(Long shopId,boolean match);


    /**
     *  获取可用的类目信息
     *
     * @param shopId 店铺id
     * @return List<CategoryVO>
     */
    List<CategoryVO> getChoosableCategoryInfo(Long shopId);

    /**
     * 查询没有设置自定义抵扣比例的类目信息
     * @param categoryId 二级分类id
     * @return 查询到的数据
     */
    List<PlatformShopSigningCategory> queryNonSigningCategory(Long categoryId);
}
