package com.medusa.gruul.addon.distribute.controller;

import com.medusa.gruul.addon.distribute.model.dto.DistributorOrderQueryDTO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorOrderPageVO;
import com.medusa.gruul.addon.distribute.service.DistributeOrderHandleService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 分校订单控制器
 *
 * @author 张治保 date 2023/5/12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/distribute/order")
public class DistributeOrderController {

    private final DistributeOrderHandleService distributeOrderHandleService;

    /**
     * 分页查询分销订单
     *
     * @param query 分销订单查询条件
     * @return 分销订单分页数据
     */
    @Log("分页查询分销订单")
    @GetMapping
    @PreAuthorize("@S.anyPerm('distribution:Index','distribution:index') or @S.matcher().any(@S.ROLES,@S.USER,@S.SHOP_CUSTOM_ADMIN,@S.SHOP_ADMIN).match()")
    public Result<DistributorOrderPageVO> orderPage(DistributorOrderQueryDTO query) {
        ISecurity.match()
                .ifUser(secureUser -> query.setCurrentUserId(secureUser.getId()))
                .ifAnyShopAdmin(secureUser -> query.setShopId(secureUser.getShopId()))
                .other(secureUser -> query.setCurrentUserId(null));
        return Result.ok(distributeOrderHandleService.orderPage(query));
    }

    @Log("导出分销订单")
    @PostMapping("/export")
    @PreAuthorize("@S.shopPerm('distribution:Index') or @S.platformPerm('distribution:Index')")
    public Result<Void> export(@RequestBody DistributorOrderQueryDTO queryDTO) {
        ISecurity.match()
                .ifUser(secureUser -> queryDTO.setCurrentUserId(secureUser.getId()))
                .ifAnyShopAdmin(secureUser -> queryDTO.setShopId(secureUser.getShopId()))
                .other(secureUser -> queryDTO.setCurrentUserId(null));
        distributeOrderHandleService.export(queryDTO);
        return Result.ok();
    }


}
