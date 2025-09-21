package com.medusa.gruul.search.service.service;

import com.medusa.gruul.search.api.model.ProductActivityVO;

/**
 * @author 张治保 date 2023/3/23
 */
public interface ProductActivityService {

    /**
     * 查询商品最近的活动
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 活动信息
     */
    ProductActivityVO getRecentActivity(Long shopId, Long productId);
}
