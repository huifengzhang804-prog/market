package com.medusa.gruul.user.service.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.user.service.model.dto.IntegralDetailQueryDTO;
import com.medusa.gruul.user.service.model.dto.UserIntegralChangeDTO;
import com.medusa.gruul.user.service.model.vo.UserIntegralDetailVO;
import com.medusa.gruul.user.service.mp.service.IUserIntegralDetailService;
import com.medusa.gruul.user.service.service.UserIntegralService;
import io.vavr.control.Option;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * @author shishuqian
 * @date 2023/2/9
 * @time 11:32
 **/

@RestController
@RequestMapping("/user/integral")
@RequiredArgsConstructor
public class UserIntegralController {


    private final UserIntegralService userIntegralService;
    private final IUserIntegralDetailService iUserIntegralDetailService;


    /**
     * 查看用户剩余积分
     */
    @Log("查看用户剩余积分")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.PLATFORM_ADMIN,@S.USER)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'vip:base'))
            .match()
            """)
    @GetMapping("/system/total")
    public Result<Long> getIntegralTotal(Long userId) {

        userId = Option.of(userId).getOrElse(() -> ISecurity.userMust().getId());

        return Result.ok(
                this.userIntegralService.getIntegralTotalByUserId(userId)
        );
    }


    /**
     * 用户积分变化
     */
    @Log("用户积分调整")
    @PreAuthorize("@S.platformPerm('vip:base')")
    @PostMapping("/system/change")
    public Result<Boolean> integralChange(@RequestBody @Valid UserIntegralChangeDTO userIntegralChangeDTO) {

        return Result.ok(
                this.userIntegralService.changeIntegralBySystem(userIntegralChangeDTO)
        );
    }


    /**
     * 用户积分明细List
     *
     * @param integralDetailQuery 查询参数
     * @return 积分明细
     */
    @Log("用户积分明细List")
    @GetMapping("/detail/info")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.PLATFORM_ADMIN,@S.USER)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'vip:base'))
            .match()
            """)
    public Result<UserIntegralDetailVO> getUserIntegralDetailList(IntegralDetailQueryDTO integralDetailQuery) {
        integralDetailQuery.setUserId(Option.of(integralDetailQuery.getUserId()).getOrElse(() -> ISecurity.userMust().getId()));
        return Result.ok(
                this.iUserIntegralDetailService.detailPage(integralDetailQuery)
        );
    }

}
