package com.medusa.gruul.addon.integral.controller;

import com.medusa.gruul.addon.integral.model.dto.IntegralRulesDTO;
import com.medusa.gruul.addon.integral.model.vo.IntegralRulesVO;
import com.medusa.gruul.addon.integral.service.ManageIntegralRuleService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 积分规则控制层
 *
 * @author xiaoq
 * @Description 积分规则控制层
 * @date 2023-02-03 16:05
 */
@RestController
@RequestMapping("/integral/rules")
@RequiredArgsConstructor
@PreAuthorize("@S.platformPerm('fatetrue:integral')")
public class IntegralRulesController {

    private final ManageIntegralRuleService manageIntegralRuleService;

    /**
     * 积分规则获取
     *
     * @return 积分规则vo
     */
    @Log("积分规则获取")
    @PreAuthorize("permitAll()")
    @GetMapping("/info")
    public Result<IntegralRulesVO> getIntegralRules() {
        return Result.ok(manageIntegralRuleService.getIntegralRule());
    }

    /**
     * 积分规则保存
     *
     * @param integralRules 积分规则dto
     * @return Result.ok();
     */
    @Log("积分规则保存")
    @PostMapping("/save")
    public Result<Void> saveIntegralRules(@RequestBody @Valid IntegralRulesDTO integralRules) {
        manageIntegralRuleService.saveIntegralRules(integralRules);
        return Result.ok();
    }

    /**
     * 积分规则修改
     *
     * @param integralRules 积分规则dto
     * @return Result.ok();
     */
    @Log("积分规则修改")
    @PostMapping("/update")
    public Result<Void> updateIntegralRules(@RequestBody @Valid IntegralRulesDTO integralRules) {
        manageIntegralRuleService.updateIntegralRules(integralRules);
        return Result.ok();
    }

}
