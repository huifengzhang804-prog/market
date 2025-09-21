package com.medusa.gruul.addon.rebate.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.rebate.model.dto.RebateOrderQueryDTO;
import com.medusa.gruul.addon.rebate.mp.entity.RebateOrder;
import com.medusa.gruul.addon.rebate.service.RebateOrderHandlerService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 返利订单 前端控制器
 * </p>
 *
 * @author WuDi
 * @since 2023-07-17
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/rebateOrder")
public class RebateOrderController {


    private final RebateOrderHandlerService rebateOrderHandlerService;


    /***
     * 分页查询消费返利订单
     * @param rebateOrderQuery 查询参数
     * @return 消费返利订单
     */
    @Log("查询订单")
    @PreAuthorize("@S.platformPerm('consumerRebates') or @S.matcher().role(@S.USER).match()")
    @GetMapping
    public Result<IPage<RebateOrder>> pageRebateOrder(RebateOrderQueryDTO rebateOrderQuery) {
        ISecurity.match()
                .ifUser(secureUser -> rebateOrderQuery.setUserId(secureUser.getId()));

        return Result.ok(
                rebateOrderHandlerService.pageRebateOrder(rebateOrderQuery)
        );
    }
    @Log("消费返利订单导出")
    @PreAuthorize("@S.platformPerm('consumerRebates')")
    @PostMapping("/export")
    public Result<Void> rebateOrderExport(@RequestBody RebateOrderQueryDTO queryDTO){
        rebateOrderHandlerService.export(queryDTO);
        return Result.ok();
    }


}
