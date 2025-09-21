package com.medusa.gruul.addon.team.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.team.model.dto.*;
import com.medusa.gruul.addon.team.model.key.TeamKey;
import com.medusa.gruul.addon.team.model.vo.TeamOrderPageVO;
import com.medusa.gruul.addon.team.model.vo.TeamOrderSummaryVO;
import com.medusa.gruul.addon.team.model.vo.TeamOrderUserPageVO;
import com.medusa.gruul.addon.team.model.vo.TeamPageVO;
import com.medusa.gruul.addon.team.service.TeamManagerService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.Final;
import com.medusa.gruul.overview.api.model.ActivityConfigDTO;
import io.vavr.control.Option;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author 张治保
 * date 2023/3/1
 */
@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("@S.shopPerm('marketingApp:group')")
@RequestMapping("/team/activity")
public class TeamManagerController {

    private final TeamManagerService teamManagerService;


    /**
     * 新增拼团活动
     *
     * @param teamActivity 活动信息
     * @return void
     */
    @PostMapping
    public Result<Void> newActivity(@RequestBody @Valid TeamDTO teamActivity) {
        teamManagerService.newActivity(ISecurity.userMust().getShopId(), teamActivity);
        return Result.ok();
    }

    /**
     * 分页查询拼团活动
     */
    @GetMapping
    @PreAuthorize("@S.anyPerm('group','marketingApp:group')")
    public Result<IPage<TeamPageVO>> activityPage(TeamPageDTO page) {
        Final<Long> shopIdRef = new Final<>();
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> shopIdRef.set(secureUser.getShopId()));
        return Result.ok(
                teamManagerService.activityPage(Option.of(shopIdRef.get()), page)
        );
    }

    /**
     * 设置活动规则
     *
     * @param activityConfigDTO 活动规则
     * @return Boolean 成功true
     */
    @Log("设置团购活动规则")
    @PostMapping("rule")
    public Result<Boolean> rule(@RequestBody ActivityConfigDTO activityConfigDTO) {
        teamManagerService.saveRule(activityConfigDTO);
        return Result.ok(Boolean.TRUE);
    }

    /**
     * 查询团购活动规则
     *
     * @return 活动规则
     */
    @Log("查询团购活动规则")
    @GetMapping("rule")
    public Result<String> queryRule() {
        String rule = teamManagerService.queryRule();
        return Result.ok(rule);
    }


    /**
     * 根据id 获取拼团活动
     */
    @GetMapping("/{activityId}")
    @PreAuthorize("@S.anyPerm('group','marketingApp:group')")
    public Result<TeamDTO> getActivity(@PathVariable Long activityId, @RequestParam(required = false) Long shopId) {
        Final<Long> shopIdRef = new Final<>(null);
        ISecurity.match()
                .ifAnySuperAdmin(secureUser -> shopIdRef.set(shopId))
                .ifAnyShopAdmin(secureUser -> shopIdRef.set(secureUser.getShopId()));
        return Result.ok(
                teamManagerService.getActivity(shopIdRef.get(), activityId)
        );
    }

    /**
     * 批量删除拼团活动
     *
     * @param teamKeys 拼团key集合
     * @return void
     */
    @PostMapping("/delete/batch")
    @PreAuthorize("@S.anyPerm('group','marketingApp:group')")
    public Result<Void> deleteActivityBatch(@RequestBody @NotNull @Size(min = 1) @Valid Set<TeamKey> teamKeys) {
        ISecurity.match()
                .ifAnyShopAdmin(
                        secureUser -> {
                            if (teamKeys.stream().anyMatch(teamKey -> !teamKey.getShopId().equals(secureUser.getShopId()))) {
                                throw new GlobalException(SystemCode.DATA_DELETE_FAILED_CODE, "非法操作");
                            }
                        }
                );
        teamManagerService.deleteActivityBatch(teamKeys);
        return Result.ok();
    }

    /**
     * 违规下架
     */
    @PostMapping("/platform/violate")
    @PreAuthorize("@S.platformPerm('group')")
    public Result<Void> violateActivity(@RequestBody @Valid ViolationDTO violationDTO) {
        teamManagerService.violateActivity(violationDTO);
        return Result.ok();
    }

    /**
     * 查询活动违规下架的原因
     *
     * @param teamActivityId 活动id
     * @return 违规下架原因
     */
    @GetMapping("illegal/{teamActivityId}/reason")
    @PreAuthorize("@S.anyPerm('group','marketingApp:group')")
    public Result<String> illegalReason(@PathVariable("teamActivityId") Long teamActivityId) {
        String reason = teamManagerService.queryIllegalReason(teamActivityId);
        return Result.ok(reason);
    }

    /**
     * 店铺下架
     */
    @PostMapping("/shop/shelf/{teamActivityId}")
    @PreAuthorize("@S.shopPerm('group')")
    public Result<Void> shopSelfOffActivity(@PathVariable("teamActivityId") Long teamActivityId) {
        teamManagerService.shopShelfOff(teamActivityId);
        return Result.ok();
    }

    /**
     * 分页查询拼团订单
     *
     * @param page 分页查询参数
     */
    @GetMapping("/order")
    public Result<IPage<TeamOrderPageVO>> orderPage(TeamOrderPageDTO page) {
        return Result.ok(
                teamManagerService.orderPage(page.setShopId(ISecurity.userMust().getShopId()))
        );
    }

    /**
     * 查询拼团订单摘要
     */
    @GetMapping("/order/summary")
    @PreAuthorize("""
                @S.matcher()
                .any(@S.ROLES,@S.SHOP_ADMIN,@S.USER)
                .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'marketingApp:group'))
                .match()
            """)
    public Result<TeamOrderSummaryVO> getOrderSummary(TeamSummaryDTO summary) {
        summary.validParam();
        ISecurity.match()
                .ifAnySuperAdmin(secureUser -> summary.setShopId(secureUser.getShopId()));
        return Result.ok(
                teamManagerService.getOrderSummary(summary)
        );
    }


    /**
     * 分页查询拼团订单用户列表
     *
     * @param query 分页查询参数
     */
    @GetMapping("/order/users")
    @PreAuthorize("""
                @S.matcher()
                .any(@S.ROLES,@S.SHOP_ADMIN,@S.USER)
                .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'marketingApp:group'))
                .match()
            """)
    public Result<IPage<TeamOrderUserPageVO>> orderUserPage(@Valid TeamOrderUserPageDTO query) {
        query.setShopId(null);
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> query.setShopId(secureUser.getShopId()));
        return Result.ok(
                teamManagerService.orderUserPage(query)
        );
    }

    /**
     * 拼团活动状态校验
     *
     * @param teamNo 拼团活动编号
     * @return void 是否通过
     */
    @GetMapping("/{teamNo}/valid")
    @PreAuthorize("@S.matcher().role(@S.USER).match()")
    public Result<Boolean> teamStatusValid(@PathVariable String teamNo) {
        return Result.ok(
                teamManagerService.teamStatusValid(ISecurity.userMust().getId(), teamNo)
        );
    }
}
