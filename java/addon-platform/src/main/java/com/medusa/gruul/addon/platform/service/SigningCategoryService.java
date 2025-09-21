package com.medusa.gruul.addon.platform.service;

import com.medusa.gruul.addon.platform.model.vo.CategoryVO;
import com.medusa.gruul.addon.platform.model.vo.SigningCategoryVO;

import java.util.List;
import java.util.Set;

/**
 *  签约类目service
 *
 * @author xiaoq
 * @Description SigningCategoryService.java
 * @date 2023-05-15 09:42
 */
public interface SigningCategoryService {
    /**
     * 删除签约类目
     *
     * @param signingCategoryIds 签约类目ids
     */
    void delSigningCategory(Set<Long> signingCategoryIds);

    /**
     * 新增签约类目
     *
     * @param currentCategoryId 签约平台雷米类目ids
     */
    void addSigningCategory(Set<Long> currentCategoryId);

    /**
     * 获取签约类目详情信息
     *
     * @param shopId 店铺id
     * @return 签约类目信息
     */
    List<SigningCategoryVO> getSigningCategoryList(Long shopId,Boolean parentFlag);

    /**
     * 获取店铺端可选的分类信息
     *
     * @param shopId 店铺id
     * @return  List<CategoryVO>
     */
    List<CategoryVO> getChoosableCategoryInfo(Long shopId);

    /**
     * 根据店铺id 清空签约类目
     *
     * @param shopIds 店铺ids
     */
    void clearShopSigningCategory(Set<Long> shopIds);

}
