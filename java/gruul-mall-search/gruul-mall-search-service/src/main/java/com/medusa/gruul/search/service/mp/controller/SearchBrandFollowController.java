package com.medusa.gruul.search.service.mp.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.search.service.model.dto.SearchBrandFollowDTO;
import com.medusa.gruul.search.service.service.BrandFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 品牌关注表 前端控制器
 *
 * @author WuDi
 * @since 2023-02-02
 */
@RestController
@RequestMapping("/search/BrandFollow")
@RequiredArgsConstructor
public class SearchBrandFollowController {

    private final BrandFollowService brandFollowService;


    /**
     * 关注/取消品牌
     * @param searchBrandFollowDTO  关注品牌DTO
     */
    @Log("关注/取消品牌")
    @PostMapping("/follow")
    public Result<Void> brandFollow(@RequestBody @Validated SearchBrandFollowDTO searchBrandFollowDTO) {
        brandFollowService.brandFollow(searchBrandFollowDTO);
        return Result.ok();
    }




}
