package com.medusa.gruul.search.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.goods.api.entity.ProductCategory;
import com.medusa.gruul.search.service.model.vo.SearchBrandDetailVO;

import java.util.List;

/**
 *
 * 商品类目信息 服务类
 *
 *
 * @author xiaoq
 * @since 2022-03-04
 */
public interface IProductCategoryService extends IService<ProductCategory> {

    /**
     * 获取二级、一级分类id
     * @param categoryThirdId 三级分类id
     * @return 三级分类集合
     */
    List<SearchBrandDetailVO.CategoryVO> getProductCategory(Long categoryThirdId);
}
