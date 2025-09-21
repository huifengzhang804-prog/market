package com.medusa.gruul.addon.freight.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.freight.model.dto.FrightConfDTO;
import com.medusa.gruul.addon.freight.model.enums.LogisticsCompanyStatus;
import com.medusa.gruul.addon.freight.model.param.FrightConfParam;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.addon.freight.mp.entity.FreightConf;
import com.medusa.gruul.addon.freight.service.FreightService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物流收发货地址管理控制层
 *
 * @author xiaoq
 * @Description FrightConfController.java
 * @date 2022-06-06 09:53
 */
@RestController
@RequestMapping("/fright")
@RequiredArgsConstructor
@PreAuthorize("@S.platformPerm('generalSet')")
public class FrightConfController {

	private final FreightService freightService;

	/**
	 * 物流公司信息
	 *
	 * @param frightConf 物流配置信息
	 * @return void
	 */
	@Idem(1000)
	@PostMapping("/add")
	public Result<Void> addFrightInfo(@RequestBody FrightConfDTO frightConf) {
		freightService.addFrightInfo(frightConf);
		return Result.ok();
	}


	/**
	 * 修改物流公司信息
	 *
	 * @param frightConf 物流配置信息
	 * @return void
	 */
	@Idem(1000)
	@PostMapping("/update")
	public Result<Void> updateFrightInfo(@RequestBody FrightConfDTO frightConf) {
		freightService.updateFrightInfo(frightConf);
		return Result.ok();
	}

	/**
	 * 批量删除 物流公司
	 *
	 * @param ids 物流公司ids
	 * @return Result.ok();
	 */
	@Idem(500)
	@DeleteMapping("/del/{ids}")
	public Result<Void> delFrightInfo(@PathVariable(name = "ids") Long[] ids) {
		freightService.deleteFrightInfo(ids);
		return Result.ok();
	}

	/**
	 * 物流公司列表
	 *
	 * @param frightConfParam 查询参数
	 * @return IPage<FrightConf>
	 */
	@GetMapping("/list")
	@PreAuthorize("permitAll()")
	public Result<IPage<FreightConf>> getFrightInfoList(FrightConfParam frightConfParam) {
		IPage<FreightConf> frightInfoList = freightService.getFrightInfoList(frightConfParam);
		return Result.ok(frightInfoList);
	}

	/**
	 * 批量禁用物流公司
	 *
	 * @param ids 物流公司ids
	 * @return Void
	 */
	@PutMapping("/batch/{logisticsCompanyStatus}")
	public Result<Void> batchForbiddenFright(@RequestBody Long[] ids, @PathVariable(name = "logisticsCompanyStatus") LogisticsCompanyStatus logisticsCompanyStatus) {
		freightService.batchForbiddenFright(ids, logisticsCompanyStatus);
		return Result.ok();
	}

	/**
	 * 获取物流配置信息 by shopId
	 *
	 * @return List<FreightConf>
	 */
	@GetMapping("/by/shopId/list")
	@PreAuthorize("permitAll()")
	public Result<List<FreightConf>> getFreightConfByShopId() {
		return Result.ok(freightService.getFreightConfByShopId());
	}


}
