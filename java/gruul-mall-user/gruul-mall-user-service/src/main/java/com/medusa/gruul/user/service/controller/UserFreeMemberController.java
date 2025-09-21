package com.medusa.gruul.user.service.controller;

import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.user.api.model.dto.MemberLabelDTO;
import com.medusa.gruul.user.service.model.dto.UserFreeMemberDTO;
import com.medusa.gruul.user.service.model.vo.FreeMemberRightsVO;
import com.medusa.gruul.user.service.mp.entity.UserFreeMember;
import com.medusa.gruul.user.service.service.FreeMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户免费会员控制层
 *
 * @author xiaoq
 * @Description UserFreeMemberController.java
 * @date 2022-11-10 18:31
 */
@Validated
@RestController
@RequiredArgsConstructor
@PreAuthorize("@S.platformPerm('vip:setting')")
@RequestMapping("/user/free/member")
public class UserFreeMemberController {

	private final FreeMemberService freeMemberService;

	/**
	 * 免费会员新增
	 *
	 * @param userFreeMemberDTO 免费会员dto
	 * @return Result<Void>
	 */
	@Log("免费会员新增")
	@Idem(1000)
	@PostMapping("save")
	public Result<Void> addFreeMember(@RequestBody @Validated UserFreeMemberDTO userFreeMemberDTO) {
		freeMemberService.addFreeMember(userFreeMemberDTO);
		return Result.ok();
	}


	/**
	 * 免费会员更新
	 *
	 * @param userFreeMemberDTO 免费会员dto
	 * @return Result<Void>
	 */
	@Log("免费会员更新")
	@Idem(1000)
	@PostMapping("update")
	public Result<Void> updateFreeMember(@RequestBody @Validated UserFreeMemberDTO userFreeMemberDTO) {
		freeMemberService.updateFreeMember(userFreeMemberDTO);
		return Result.ok();
	}

	/**
	 * 免费会员删除
	 *
	 * @param id 免费会员id
	 * @return Result<Void>
	 */
	@Log("免费会员删除")
	@DeleteMapping("{id}")
	public Result<Void> delFreeMember(@PathVariable("id") Long id) {
		freeMemberService.delFreeMember(id);
		return Result.ok();
	}


	/**
	 * 免费会员列表
	 *
	 * @return List<免费级别会员权益VO>
	 */
	@Log("免费会员列表")
	@GetMapping("/list")
	public Result<List<FreeMemberRightsVO>> getFreeMemberList() {
		List<FreeMemberRightsVO> freeMemberRightsList = freeMemberService.getFreeMemberList();
		return Result.ok(freeMemberRightsList);
	}


	/**
	 * 免费会员级别信息
	 *
	 * @param id 免费会员级别id
	 * @return FreeMemberRightsVO
	 */
	@Log("免费会员信息")
	@GetMapping("info")
	public Result<FreeMemberRightsVO> getFreeMemberInfo(Long id) {
		return Result.ok(freeMemberService.getFreeMemberInfo(id));
	}


	/**
	 * 根据rankCode 获取下一级别会员信息
	 *
	 * @param rankCode 会员级别
	 * @return 下一级别会员信息
	 */
	@Log("免费会员获取下一级会员信息")
	@GetMapping("next")
	@PreAuthorize("""
			@S.matcher()
			.any(@S.ROLES,@S.PLATFORM_ADMIN,@S.USER)
			.or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'vip:setting'))
			.match()
			""")
	public Result<UserFreeMember> getNextLevelMemberInfo(Integer rankCode) {
		return Result.ok(freeMemberService.getNextLevelMemberInfo(rankCode));
	}

	/**
	 * 免费会员设置标签
	 *
	 * @param dto 标签DTO
	 */
	@Log("免费会员设置标签")
	@Idem(1000)
	@PostMapping("/saveLabel")
	public Result<Void> addFreeMemberLabel(@RequestBody @Validated MemberLabelDTO dto) {
		freeMemberService.addFreeMemberLabel(dto);
		return Result.ok();
	}
}
