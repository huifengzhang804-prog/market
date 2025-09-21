package com.medusa.gruul.user.service.controller;


import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.user.api.model.UserTagVo;
import com.medusa.gruul.user.service.model.dto.UserTagDTO;
import com.medusa.gruul.user.service.service.UserTagManageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员标签 前端控制器
 *
 * @author WuDi
 * @since 2022-09-14
 */
@RestController
@RequestMapping("/user/userTag")
@RequiredArgsConstructor
public class UserTagController {

    private final UserTagManageService memberTagService;


    /**
     * 查询会员所有标签
     *
     * @param bound 是否只查询被用户绑定的标签
     * @return 会员标签集合
     */
    @Log("查询所有标签")
    @GetMapping
    public Result<List<UserTagVo>> getUserTagList(@RequestParam(required = false) Boolean bound) {
        return Result.ok(
                memberTagService.getUserTagList(bound)
        );
    }

    /**
     * 设置会员标签
     *
     * @param userTag 会员标签
     */
    @Log("设置会员标签")
    @PostMapping
    @PreAuthorize("@S.platformPerm('vip:base') or @S.shopPerm()")
    public Result<Void> updateUserTag(@RequestBody @Valid UserTagDTO userTag) {
        memberTagService.updateUserTag(userTag);
        return Result.ok();
    }
}

