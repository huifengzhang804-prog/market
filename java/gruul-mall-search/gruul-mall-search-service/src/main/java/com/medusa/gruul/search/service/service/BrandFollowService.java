package com.medusa.gruul.search.service.service;

import com.medusa.gruul.search.service.model.dto.SearchBrandFollowDTO;

public interface BrandFollowService {

    /**
     * 关注/取消品牌
     * @param searchBrandFollowDTO  关注品牌DTO
     */
    void brandFollow(SearchBrandFollowDTO searchBrandFollowDTO);
}
