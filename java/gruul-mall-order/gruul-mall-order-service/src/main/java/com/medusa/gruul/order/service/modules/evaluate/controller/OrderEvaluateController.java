package com.medusa.gruul.order.service.modules.evaluate.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.order.api.entity.OrderEvaluate;
import com.medusa.gruul.order.service.model.dto.EvaluateQueryDTO;
import com.medusa.gruul.order.service.model.dto.OrderEvaluateDTO;
import com.medusa.gruul.order.service.model.dto.ProductEvaluateQueryDTO;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.model.vo.ProductEvaluateOverviewVO;
import com.medusa.gruul.order.service.modules.evaluate.model.EvaluateDetailKey;
import com.medusa.gruul.order.service.modules.evaluate.service.EvaluateService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前端控制器
 *
 * @author WuDi
 * @since 2022-08-09
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/order/evaluate")
public class OrderEvaluateController {


    private final EvaluateService evaluateService;

    /**
     * 分页查询真实评价
     *
     * @param evaluateQuery 用户订单评价查询条件
     * @return 评价
     */
    @Log("分页查询真实评价")
    @GetMapping
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.SHOP_ADMIN,@S.USER)
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'order:appraise'))
            .match()
            """)
    public Result<IPage<OrderEvaluate>> trueEvaluatePage(EvaluateQueryDTO evaluateQuery) {
        return Result.ok(
                evaluateService.trueEvaluatePage(evaluateQuery)
        );
    }

    /**
     * 用户评价订单
     *
     * @param orderEvaluate 订单评价
     */
    @Log("用户评价订单")
    @Idem
    @PreAuthorize(value = "@S.matcher().eq(@S.ROLES,@S.USER).match()")
    @PostMapping
    public Result<Void> evaluateOrder(@RequestBody @Validated OrderEvaluateDTO orderEvaluate) {
        // 黑名单用户 提示语
        OrderError.USER_CAN_NOT_COMMENT.trueThrow(ISecurity.userMust().getSubRoles().contains(Roles.FORBIDDEN_COMMENT));
        evaluateService.evaluate(Boolean.FALSE, ISecurity.userMust().getId(), orderEvaluate);
        return Result.ok();
    }

    /**
     * 商品详情页评价概况
     *
     * @param productEvaluateQuery 用户订单评价查询条件
     */
    @Log("商品详情页评价概况")
    @GetMapping("/shop/{shopId}/product/{productId}/overview")
    public Result<ProductEvaluateOverviewVO> productEvaluateOverview(ProductEvaluateQueryDTO productEvaluateQuery) {
        return Result.ok(
                evaluateService.productEvaluateOverview(productEvaluateQuery)
        );
    }

    /**
     * 商品详情页 分页查询评价
     *
     * @param productEvaluateQuery 用户订单评价查询条件
     */
    @Log("商品详情页 分页查询评价")
    @GetMapping("/shop/{shopId}/product/{productId}")
    public Result<IPage<OrderEvaluate>> productEvaluatePage(ProductEvaluateQueryDTO productEvaluateQuery) {
        return Result.ok(
                evaluateService.productEvaluatePage(productEvaluateQuery)
        );
    }

    /**
     * 批量设为精选/取消精选
     *
     * @param evaluateIds 评价ids
     * @param isExcellent 设为精选/取消精选
     */
    @Log("批量设为精选/取消精选")
    @PreAuthorize("@S.shopPerm('order:appraise')")
    @PutMapping("/excellent/{isExcellent}")
    public Result<Void> setEvaluateExcellent(@RequestBody List<Long> evaluateIds, @PathVariable Boolean isExcellent) {
        evaluateService.setEvaluateExcellent(evaluateIds, isExcellent);
        return Result.ok();
    }

    /**
     * 商家回复评价
     *
     * @param evaluateId 评价id
     * @param reply      商家回复内容
     */
    @Log("商家回复评价")
    @PutMapping("/{evaluateId}/reply")
    @PreAuthorize("@S.shopPerm('order:appraise')")
    public Result<Void> replyEvaluate(@PathVariable Long evaluateId, @RequestBody @NotBlank String reply) {
        evaluateService.replyEvaluate(evaluateId, reply);
        return Result.ok();
    }

    /**
     * 查询订单商品评价
     *
     * @param orderNo 订单号
     * @param key     商品sku key信息
     */
    @Log("查询订单商品评价详情")
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    @PostMapping("/{orderNo}")
    public Result<OrderEvaluate> getOrderEvaluate(@PathVariable String orderNo, @RequestBody EvaluateDetailKey key) {
        return Result.ok(
                evaluateService.getOrderEvaluate(orderNo, key)
        );
    }

}

