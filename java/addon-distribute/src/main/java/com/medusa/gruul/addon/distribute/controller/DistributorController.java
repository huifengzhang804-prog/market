package com.medusa.gruul.addon.distribute.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.distribute.model.dto.ApplyAffairsDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorQueryDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorRankDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorTeamQueryDTO;
import com.medusa.gruul.addon.distribute.model.enums.DistributorStatus;
import com.medusa.gruul.addon.distribute.model.vo.DistributorApplyStatusVO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorAuditVo;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;
import com.medusa.gruul.addon.distribute.service.DistributorHandleService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Set;

/**
 * 分销商控制器
 *
 * @author 张治保
 * date 2022/11/16
 */
@RestController
@RequiredArgsConstructor
@Validated
@PreAuthorize("@S.matcher().role(@S.USER).match()")
@RequestMapping("/distribute/distributor")
public class DistributorController {

    private final DistributorHandleService distributorHandleService;

    /**
     * 查询我的分销码
     *
     * @return 分校码
     */
    @Log("查询我的分销码")
    @GetMapping("/mine")
    public Result<String> getCode() {
        return Result.ok(
                distributorHandleService.getCode(ISecurity.userMust().getId()).getOrNull()
        );
    }

    /**
     * 查询我的申请状态
     *
     * @return 我的申请状态
     */
    @Log("查询我的申请状态")
    @GetMapping("/mine/status")
    public Result<DistributorApplyStatusVO> applyStatus() {
        return Result.ok(
                distributorHandleService.applyStatus(ISecurity.userMust().getId())
        );
    }

    /**
     * 查询我的分销商资料信息
     *
     * @return 我的分销商资料信息
     */
    @Log("查询我的分销商信息资料")
    @GetMapping("/mine/statistics")
    public Result<Distributor> getMyAffairsInfo() {
        return Result.ok(
                distributorHandleService.getAffairsInfoByUserId(ISecurity.userMust().getId())
        );
    }

    /**
     * 用户扫码之后成为分销员 推荐人为当前code所属分销商
     *
     * @param code 推荐人 专属 分销码
     * @return void
     */
    @Idem(500)
    @Log("用户扫码成为分销员")
    @PostMapping("/code/{code}")
    public Result<Void> scanCode(@PathVariable @NotBlank @Length(min = 14, max = 14) String code) {
        distributorHandleService.scanCode(ISecurity.userMust().getId(), code);
        return Result.ok();
    }

    /**
     * 提交申请成为分销商的表单
     *
     * @param applyAffairs 申请分销商表单
     * @return void
     */
    @Idem
    @Log("提交申请成为分销商的表单")
    @PostMapping("/affairs/apply")
    public Result<Void> applyAffairs(@RequestBody @Valid ApplyAffairsDTO applyAffairs) {
        SecureUser<?> secureUser = ISecurity.userMust();
        distributorHandleService.applyAffairs(secureUser.getId(), secureUser.getOpenid(), applyAffairs);
        return Result.ok();
    }

    @Log("查询拒绝原因")
    @GetMapping("/rejectReason/{id}")
    @PreAuthorize("@S.platformPerm('distribution:index')")
    public Result<String> queryRejectReason(@PathVariable("id") Long id) {
        return Result.ok(distributorHandleService.queryRejectReason(id));
    }

    /**
     * 分页查询分销商
     *
     * @param query 分销商查询条件
     * @return 分页查询结果
     */
    @Log("分页查询分销商")
    @GetMapping
    @PreAuthorize("@S.anyPerm('distribution:index','sales:distribute') or @S.matcher().any(@S.ROLES,@S.USER,@S.SHOP_CUSTOM_ADMIN,@S.SHOP_ADMIN).match()")
    public Result<IPage<Distributor>> distributorPage(DistributorQueryDTO query) {
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> query.setShopId(secureUser.getShopId()));
        return Result.ok(distributorHandleService.distributorPage(query));
    }

    /**
     * 分页查询分销排行榜
     *
     * @param query 分销商查询条件
     * @return 分页查询结果
     */
    @GetMapping("/rank")
    @PreAuthorize("@S.anyPerm('distribution:index','sales:distribute') or @S.matcher().role(@S.USER).match()")
    public Result<IPage<Distributor>> rank(DistributorRankDTO query) {
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> query.setShopId(secureUser.getShopId()))
                .ifUser(secureUser -> query.setUserId(secureUser.getId()));
        return Result.ok(distributorHandleService.rank(query));
    }


    /**
     * 根据分销商用户id查询下线分销员
     *
     * @param query 分销员查询条件
     * @return 分销员分页数据
     */
    @Log("根据分销商用户id查询下线分销员")
    @GetMapping("/team")
    @PreAuthorize("@S.anyPerm('distribution:index','sales:distribute') or @S.matcher().role(@S.USER).match()")
    public Result<IPage<Distributor>> distributorTeamPage(DistributorTeamQueryDTO query) {
        ISecurity.match()
                .ifUser(secureUser -> query.setUserId(secureUser.getId()));
        if (query.getUserId() == null) {
            return Result.ok();
        }
        return Result.ok(distributorHandleService.distributorTeamPage(query));
    }

    /**
     * 批量通过/关闭用户申请
     *
     * @return void
     */
    @Log("批量通过/关闭用户申请")
    @PreAuthorize("@S.platformPerm('distribution:index')")
    @PostMapping("/distributor/apply")
    public Result<Void> applyAudit(@RequestBody @Valid DistributorAuditVo distributorAuditVo) {
        if (!distributorAuditVo.getPass() && Objects.isNull(distributorAuditVo.getRejectReason())) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
        distributorHandleService.applyAudit(distributorAuditVo);
        return Result.ok();
    }

    /**
     * 批量删除用户申请
     *
     * @param userIds 用户id集合
     * @return void
     */
    @Log("批量删除用户申请")
    @DeleteMapping("/distributor/apply")
    @PreAuthorize("@S.platformPerm('distribution:index')")
    public Result<Void> applyRemove(@RequestBody @NotNull @Size(min = 1) Set<Long> userIds) {
        userIds.forEach(
                userId -> distributorHandleService.updateDistributorStatus(userId, DistributorStatus.CLOSED, DistributorStatus.NOT_APPLIED)
        );
        return Result.ok();
    }

    /**
     * 查询上级分销商信息
     *
     * @param userId 用户id
     * @return 上级分销商信息
     */
    @Log("查询上级分销商信息")
    @PreAuthorize("@S.platformPerm('vip:base')")
    @GetMapping("/distributor/parent/{userId}")
    public Result<Distributor> getParentDistributorByUserId(@PathVariable Long userId) {
        return Result.ok(
                distributorHandleService.getParentDistributorByUserId(userId).getOrNull()
        );
    }
}
