package com.medusa.gruul.user.service.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.user.api.model.vo.MemberAggregationInfoVO;
import com.medusa.gruul.user.service.service.MemberCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户会员卡控制层
 *
 * @author xiaoq
 * @Description UserMemberCardController.java
 * @date 2022-11-17 10:38
 */
@RestController
@RequestMapping("/user/member/card")
@RequiredArgsConstructor
public class UserMemberCardController {

    private final MemberCardService memberCardService;


    /**
     * 获取会员中心信息
     *
     * @return 会员中心信息
     */
    @Log("会员中心")
    @GetMapping("/info")
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    public Result<MemberAggregationInfoVO> memberCentre(Long userId) {
        return Result.ok(memberCardService.getMemberCentre(userId));
    }




}
