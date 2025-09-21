package com.medusa.gruul.addon.member.controller;

import com.medusa.gruul.addon.member.model.dto.PaidMemberDTO;
import com.medusa.gruul.addon.member.model.dto.PayDTO;
import com.medusa.gruul.addon.member.model.vo.PaidMemberRightsVO;
import com.medusa.gruul.addon.member.service.ManagePaidMemberService;
import com.medusa.gruul.addon.member.service.MovePaidMemberService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import com.medusa.gruul.user.api.model.dto.MemberLabelDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 付费会员控制层
 *
 * @author xiaoq
 * date 2022-11-15 10:30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/paid/member")
@PreAuthorize("@S.platformPerm('vip:setting')")
public class PaidMemberController {

    private final ManagePaidMemberService managePaidMemberService;

    private final MovePaidMemberService movePaidMemberService;


    /**
     * 付费会员新增
     *
     * @param paidMember 付费会员DTO
     * @return Result<Void>
     */
    @Log("付费会员新增")
    @Idem(1000)
    @PostMapping("save")
    public Result<Void> addPaidMember(@RequestBody @Validated PaidMemberDTO paidMember) {
        managePaidMemberService.addPaidMember(paidMember);
        return Result.ok();
    }

    /**
     * 付费会员更新
     *
     * @param paidMember 付费会员DTO
     * @return Result<Void>
     */
    @Log("付费会员更新")
    @Idem(1000)
    @PostMapping("update")
    public Result<Void> updatePaidMember(@RequestBody @Validated PaidMemberDTO paidMember) {
        managePaidMemberService.updatePaidMember(paidMember);
        return Result.ok();
    }


    /**
     * 付费会员删除
     *
     * @param id 付费会员id
     * @return Result<Void>
     */
    @Log("付费会员删除")
    @DeleteMapping("{id}")
    public Result<Void> delPaidMember(@PathVariable("id") Long id) {
        managePaidMemberService.delPaidMember(id);
        return Result.ok();
    }


    /**
     * 付费会员列表
     *
     * @return List<付费级别会员权益VO>
     */
    @Log("付费会员列表")
    @GetMapping("list")
    public Result<List<PaidMemberRightsVO>> getPaidMemberList() {
        return Result.ok(managePaidMemberService.getPaidMemberList());
    }


    /**
     * 付费会员配置详情
     *
     * @param id 付费会员级别id
     * @return 付费会员配置详情
     */
    @Log("付费会员配置详情")
    @GetMapping("info")
    public Result<PaidMemberRightsVO> getPaidMemberInfo(Long id) {
        return Result.ok(managePaidMemberService.getPaidMemberInfo(id));
    }

    /**
     * 付费会员标签设置
     *
     * @param dto 付费会员DTO
     * @return Result<Void>
     */
    @Log("付费会员标签设置")
    @Idem(1000)
    @PostMapping("saveLabel")
    public Result<Void> savePaidMemberLabel(@RequestBody @Validated MemberLabelDTO dto) {
        managePaidMemberService.savePaidMemberLabel(dto);
        return Result.ok();
    }

//========================================================移动端============================================
//========================================================移动端============================================

    /**
     * 付费会员级别列表----移动端
     *
     * @return 付费会员级别列表信息
     */
    @Log("付费会员级别列表")
    @PreAuthorize("permitAll()")
    @GetMapping("rank")
    public Result<List<PaidMemberRightsVO>> getRankPaidMemberInfo() {
        return Result.ok(movePaidMemberService.getRankPaidMemberInfo());
    }


    /**
     * 付费会员购买
     *
     * @param pay 支付信息
     * @return 业务支付结果
     */
    @Log("付费会员购买")
    @PostMapping("pay")
    @PreAuthorize("@S.matcher().role(@S.USER).match()")
    public Result<PayResult> openPaidMember(@RequestBody @Validated PayDTO pay) {
        return Result.ok(movePaidMemberService.openPaidMember(pay));
    }

}
