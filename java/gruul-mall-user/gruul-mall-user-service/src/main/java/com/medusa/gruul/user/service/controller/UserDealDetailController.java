package com.medusa.gruul.user.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.user.service.mp.entity.UserDealDetail;
import com.medusa.gruul.user.service.service.UserOrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户交易明细控制层
 *
 * @author xiaoq
 * @Description
 * @date 2022-11-29 09:28
 */
@RestController
@RequestMapping("/user/deal/detail")
@PreAuthorize("@S.platformPerm('vip:base') or @S.shopPerm()")
@RequiredArgsConstructor
public class UserDealDetailController {

	private final UserOrderDetailService userOrderDetailService;

	/**
	 * 用户交易明细列表
	 *
	 * @return 分页交易情况
	 */
	@Log("用户交易明细明细列表")
	@GetMapping("/list/{userId}")
	public Result<IPage<UserDealDetail>> getUserDealDetailsList(@PathVariable("userId") Long userId, Page<UserDealDetail> page) {
		return Result.ok(userOrderDetailService.getUserDealDetailsList(userId, page));
	}
}
