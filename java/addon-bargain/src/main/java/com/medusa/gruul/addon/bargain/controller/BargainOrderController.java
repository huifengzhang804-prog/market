package com.medusa.gruul.addon.bargain.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.bargain.model.dto.BargainOrderQueryDTO;
import com.medusa.gruul.addon.bargain.model.vo.BargainOrderVO;
import com.medusa.gruul.addon.bargain.mp.entity.BargainOrder;
import com.medusa.gruul.addon.bargain.mp.service.IBargainOrderService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 砍价订单 前端控制器
 *
 * @author WuDi
 * @since 2023-03-14
 */
@RestController
@RequestMapping("/bargainOrder")
@RequiredArgsConstructor
public class BargainOrderController {

    private final IBargainOrderService bargainOrderService;

    /**
     * 分页查询砍价订单
     *
     * @param bargainOrderQuery 砍价订单查询参数
     * @return 砍价订单分页数据
     */
    @Log("分页查询砍价订单")
    @GetMapping
    @PreAuthorize("@S.shopPerm('marketingApp:bargain')")
    public Result<IPage<BargainOrder>> getBargainOrderPage(BargainOrderQueryDTO bargainOrderQuery) {
        return Result.ok(
                bargainOrderService.getBargainOrderPage(bargainOrderQuery)
        );
    }
    /**
     * 砍价订单详情
     *
     * @param id 砍价订单id
     * @return 砍价订单详情
     */
    @Log("砍价订单详情")
    @GetMapping("/detail/{id}")
    @PreAuthorize("@S.shopPerm('marketingApp:bargain')")
    public Result<BargainOrderVO> getBargainOrderDetail(@PathVariable Long id) {
        return Result.ok(
                bargainOrderService.getBargainOrderDetail(ISecurity.userMust().getShopId(),id)
        );
    }

}
