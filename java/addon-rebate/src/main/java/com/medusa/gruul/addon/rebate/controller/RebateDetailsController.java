package com.medusa.gruul.addon.rebate.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.rebate.model.dto.RebateDetailsQueryDTO;
import com.medusa.gruul.addon.rebate.mp.entity.RebateDetails;
import com.medusa.gruul.addon.rebate.service.RebateDetailsHandlerService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 返利明细 前端控制器
 *
 * @author WuDi
 * @since 2023-07-17
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/rebateDetails")
public class RebateDetailsController {

    private final RebateDetailsHandlerService rebateDetailsHandlerService;

    /**
     * 分页查询返利明细
     *
     * @param rebateDetailsQuery 查询参数
     * @return 返利明细
     */
    @Log("返利明细")
    @GetMapping
    @PreAuthorize("@S.matcher().role(@S.USER).match()")
    public Result<IPage<RebateDetails>> rebateDetailsPage(RebateDetailsQueryDTO rebateDetailsQuery) {
        return Result.ok(
                rebateDetailsHandlerService.rebateDetailsPage(rebateDetailsQuery.setUserId(ISecurity.userMust().getId()))
        );
    }

}
