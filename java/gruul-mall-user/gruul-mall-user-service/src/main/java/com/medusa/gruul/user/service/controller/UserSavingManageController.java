package com.medusa.gruul.user.service.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.user.service.model.dto.UserSavingRuleDTO;
import com.medusa.gruul.user.service.mp.entity.UserSavingRule;
import com.medusa.gruul.user.service.service.UserSavingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户储蓄管理控制层
 *
 * @author xiaoq
 * @Description
 * @date 2022-09-01 13:50
 */
@RestController
@RequiredArgsConstructor
@PreAuthorize("@S.platformPerm('vip:money')")
@RequestMapping("/user/saving/manage")
public class UserSavingManageController {

	private final UserSavingService userSavingService;


	/**
	 * 获取储值管理信息
	 */
	@Log("获取储值管理信息")
	@GetMapping("/get")
	@PreAuthorize("permitAll()")
	public Result<UserSavingRule> getSavingManageInfo() {
		UserSavingRule userSavingRule = userSavingService.getSavingManageInfo();
		return Result.ok(userSavingRule);
	}

	/**
	 * 编辑储值管理信息
	 */
	@Log("编辑储值管理信息")
	@PostMapping("/edit")
	public Result<Void> editSavingManageInfo(@RequestBody UserSavingRuleDTO userSavingRule) {
		userSavingService.editSavingManageInfo(userSavingRule);
		return Result.ok();
	}

	/**
	 * 修改储值管理信息开关
	 */
	@Log("修改储值管理信息开关")
	@PutMapping("/update/{status}")
	public Result<Void> setSavingManageSwitch(@PathVariable Boolean status) {
		userSavingService.setSavingManageSwitch(status);
		return Result.ok();
	}

}
