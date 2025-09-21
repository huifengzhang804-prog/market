package com.medusa.gruul.user.service.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import com.medusa.gruul.user.service.model.dto.UserPayDTO;
import com.medusa.gruul.user.service.service.UserSavingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户储蓄控制层
 *
 * @author xiaoq
 * @Description UserSavingController.java
 * @date 2022-09-01 13:44
 */
@RestController
@RequestMapping("/user/saving")
@PreAuthorize("@S.matcher().anyRole(@S.R.USER,@S.PLATFORM_ADMIN).or(@S.consumer().role(@S.PLATFORM_CUSTOM_ADMIN).perm('vip:base')).match()")
@RequiredArgsConstructor
public class UserSavingController {

    private final UserSavingService userSavingService;

    /**
     * 用户储值充值
     *
     * @param userPay 用户储值
     */
    @Log("用户储值充值")
    @PostMapping("/pay")
    public Result<PayResult> userSavingPay(@RequestBody @Validated UserPayDTO userPay) {
        return Result.ok(userSavingService.userSavingPay(userPay));
    }


}
