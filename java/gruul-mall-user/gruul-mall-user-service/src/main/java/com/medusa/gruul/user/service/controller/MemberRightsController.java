package com.medusa.gruul.user.service.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.user.api.enums.RightsType;
import com.medusa.gruul.user.api.model.dto.MemberRightsDTO;
import com.medusa.gruul.user.service.mp.entity.MemberRights;
import com.medusa.gruul.user.service.service.UserMemberRightsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户会员权益控制层
 *
 * @author xiaoq
 * @date 2022-11-09 15:16
 */
@RestController
@PreAuthorize("@S.platformPerm('vip:setting')")
@RequiredArgsConstructor
@RequestMapping("/user/member/rights")
public class MemberRightsController {

	private final UserMemberRightsService userMemberRightsService;

	/**
	 * 会员权益获取
	 *
	 * @return 会员权益
	 */
	@Log("会员权益获取")
	@Idem(200)
	@GetMapping
	public Result<Page<MemberRights>> getMemberRightsList(Page<MemberRights> page) {
		return Result.ok(userMemberRightsService.getMemberRightsList(page));
	}

	/**
	 * 会员权益修改
	 *
	 * @return Result<Void>
	 */
	@Idem(1000)
	@Log("会员权益修改")
	@PostMapping("update")
	public Result<Void> updateMemberRights(@RequestBody MemberRightsDTO memberRights) {
		userMemberRightsService.updateMemberRights(memberRights);
		return Result.ok();
	}

	/**
	 * 会员权益新增
	 *
	 * @return Result<Void>
	 */
	@Idem(1000)
	@Log("会员权益新增")
	@PostMapping("save")
	public Result<Void> saveMemberRights(@RequestBody @Validated MemberRightsDTO memberRights) {
		userMemberRightsService.saveMemberRights(memberRights);
		return Result.ok();
	}


	/**
	 * 会员权益删除
	 *
	 * @return Result<Void>
	 */
	@Log("会员权益删除")
	@DeleteMapping("del/{ids}")
	public Result<Void> delMemberRights(@PathVariable("ids") Long[] ids) {
		userMemberRightsService.delMemberRights(ids);
		return Result.ok();
	}


	/**
	 * 会员权益开启或者关闭
	 *
	 * @return Result<Void>
	 */
	@Idem(1000)
	@Log("会员权益开启或关闭")
	@PutMapping("{rightsSwitch}")
	public Result<Void> memberRightsSwitch(@PathVariable("rightsSwitch") Boolean rightsSwitch, Long id) {
		userMemberRightsService.memberRightsSwitch(rightsSwitch, id);
		return Result.ok();
	}

	/**
	 * 可用会员权益获取
	 *
	 * @return List<MemberRights>
	 */
	@Log("可用会员权益获取")
	@GetMapping("usable")
	public Result<List<MemberRights>> getUsableMemberRightsList() {
		List<MemberRights> usableMemberRightsList = userMemberRightsService.getUsableMemberRightsList();
		return Result.ok(usableMemberRightsList);
	}

	/**
	 * 会员权益恢复默认
	 *
	 * @param rightsType 会员权益
	 * @return 会员权益默认值
	 */
	@Log("权益恢复默认")
	@GetMapping("default")
	public Result<MemberRights> memberRightsRestoreDefault(RightsType rightsType) {
		MemberRights memberRights = userMemberRightsService.memberRightsRestoreDefault(rightsType);
		return Result.ok(memberRights);
	}

}
