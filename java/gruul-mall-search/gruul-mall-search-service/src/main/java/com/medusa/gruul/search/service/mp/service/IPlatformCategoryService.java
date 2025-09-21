package com.medusa.gruul.search.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.search.service.model.vo.SearchBrandDetailVO;
import com.medusa.gruul.search.service.mp.entity.PlatformCategory;

import java.util.List;

/**
 * @author: WuDi
 * @date: 2022/10/28
 */
public interface IPlatformCategoryService extends IService<PlatformCategory> {



    /**
     * 根据三级类目id获取二级类目和一级类目id
     *
     * @param parentCategoryId 三级类目id
     * @return 平台三级类目
     */
  List<SearchBrandDetailVO.CategoryVO> getPlatformCategory(Long parentCategoryId);
}
