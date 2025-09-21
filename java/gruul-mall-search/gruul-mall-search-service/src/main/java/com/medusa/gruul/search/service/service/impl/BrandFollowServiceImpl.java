package com.medusa.gruul.search.service.service.impl;

import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.search.service.model.dto.SearchBrandFollowDTO;
import com.medusa.gruul.search.service.mp.entity.SearchBrandFollow;
import com.medusa.gruul.search.service.mp.service.ISearchBrandFollowService;
import com.medusa.gruul.search.service.service.BrandFollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class BrandFollowServiceImpl implements BrandFollowService {


    private final ISearchBrandFollowService brandFollowService;

    /**
     * 关注/取消品牌
     *
     * @param brandFollow 关注品牌
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void brandFollow(SearchBrandFollowDTO brandFollow) {
        Boolean isFollow = brandFollow.getIsFollow();
        Long userId = ISecurity.userMust().getId();
        Long brandId = brandFollow.getBrandId();
        if (isFollow) {
            SearchBrandFollow searchBrandFollow = new SearchBrandFollow();
            searchBrandFollow.setUserId(userId)
                    .setBrandId(brandId);
            boolean save = brandFollowService.save(searchBrandFollow);
            if (!save) {
                throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "关注品牌失败");
            }
            return;
        }
        boolean remove = brandFollowService.lambdaUpdate()
                .eq(SearchBrandFollow::getBrandId, brandId)
                .eq(SearchBrandFollow::getUserId, userId)
                .remove();
        if (!remove) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, "取消关注品牌失败");
        }
    }
}
