package com.medusa.gruul.addon.integral.controller;

import com.medusa.gruul.addon.integral.model.enums.GainRuleType;
import com.medusa.gruul.addon.integral.model.vo.IntegralBehaviorVO;
import com.medusa.gruul.addon.integral.service.ClientIntegralBehaviorService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 积分行为控制层
 *
 * @author xiaoq
 * @Description IntegralBehaviorController.java
 * @date 2023-02-06 15:37
 */
@RestController
@RequestMapping("/integral/behavior")
@RequiredArgsConstructor
@PreAuthorize("@S.matcher().role(@S.USER).match()")
public class IntegralBehaviorController {

    private final ClientIntegralBehaviorService clientIntegralBehaviorService;

    /**
     * 积分行为触发
     *
     * @param ruleType 积分规则类型
     * @return 积分值
     */
    @GetMapping("/save")
    @Log("积分行为触发")
    public Result<Integer> saveIntegralBehavior(GainRuleType ruleType) {
        //获取登录用户信息
        SecureUser secureUser = ISecurity.userMust();
        return Result.ok(
                clientIntegralBehaviorService.saveIntegralBehavior(ruleType, secureUser)
        );
    }

    /**
     * 获取积分行为的连续天数（连续登录，签到...的天数）
     *
     * @param ruleType 查询类型
     * @return IntegralBehaviorVO 包含连续天数和当前是否已触发
     */
    @GetMapping("/days")
    @Log("积分行为连续天数")
    public Result<IntegralBehaviorVO> getContinueDays(GainRuleType ruleType) {
        //获取登录用户信息
        SecureUser secureUser = ISecurity.userMust();
        return Result.ok(
                this.clientIntegralBehaviorService.getIntegralBehaviorContinueDays(ruleType, secureUser)
        );
    }

}
