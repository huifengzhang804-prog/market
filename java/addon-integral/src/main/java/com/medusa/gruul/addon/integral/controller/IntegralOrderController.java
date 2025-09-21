package com.medusa.gruul.addon.integral.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.integral.model.bo.IntegralOrderDetailInfoBO;
import com.medusa.gruul.addon.integral.model.dto.IntegralOrderDTO;
import com.medusa.gruul.addon.integral.model.dto.IntegralOrderQueryDTO;
import com.medusa.gruul.addon.integral.model.dto.IntegralOrderRemarkDTO;
import com.medusa.gruul.addon.integral.model.vo.IntegralOrderListVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderService;
import com.medusa.gruul.addon.integral.service.CloseIntegralOrderService;
import com.medusa.gruul.addon.integral.service.CreateIntegralOrderService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 积分订单控制层
 *
 * @author xiaoq
 * @Description 积分订单控制层
 * @date 2023-01-31 15:12
 */
@RestController
@RequestMapping("/integral/order")
@RequiredArgsConstructor
public class IntegralOrderController {

    private final CreateIntegralOrderService createIntegralOrderService;

    private final IIntegralOrderService iIntegralOrderService;

    private final CloseIntegralOrderService closeIntegralOrderService;

    /**
     * 创建未支付的积分订单
     *
     * @return Result.ok(积分订单号)
     */
    @PostMapping("/create")
    @Log("创建未支付的积分订单并返回订单号")
    @PreAuthorize(value = "@S.matcher().eq(@S.ROLES, @S.USER).neq(@S.ROLES, @S.R.FORBIDDEN_ORDER).match()")
    public Result<String> createUnpaidIntegralOrder(@RequestBody @Valid IntegralOrderDTO integralOrderDTO) {
        //获取登录用户信息
        SecureUser<?> secureUser = ISecurity.userMust();
        return Result.ok(
                this.createIntegralOrderService.create(integralOrderDTO, secureUser)
        );
    }


    /**
     * 积分订单详情 by 订单号
     *
     * @return Result.ok(积分订单详细信息)
     */
    @GetMapping("/get/{integralOrderNo}")
    @Log("积分订单详情信息 by 订单号")
    public Result<IntegralOrder> getIntegralOrderInfo(@PathVariable("integralOrderNo") String integralOrderNo) {
        return Result.ok(
                this.iIntegralOrderService.getIntegralOrderDetailInfo(integralOrderNo)
        );
    }


    /**
     * 积分订单列表
     */
    @GetMapping("/list")
    @Log("积分订单列表")
    public Result<IPage<IntegralOrderListVO>> getIntegralOrderList(IntegralOrderQueryDTO integralOrderQueryDTO) {
        ISecurity.match()
                .ifUser(secureUser -> integralOrderQueryDTO.setBuyerId(secureUser.getId()));
        return Result.ok(
                this.iIntegralOrderService.integralOrderPage(integralOrderQueryDTO)
        );
    }


    /**
     * 积分订单备注
     *
     * @param integralOrderRemark 积分订单备注DTO
     * @return Result.ok()
     */
    @Idem(500)
    @Log("批量备注")
    @PutMapping("/remark/batch")
    public Result<Void> integralOrderRemark(@RequestBody @Valid IntegralOrderRemarkDTO integralOrderRemark) {
        this.iIntegralOrderService.integralOrderBatchRemark(integralOrderRemark);
        return Result.ok();
    }


    /**
     * 查询订单的创建情况
     *
     * @param orderNo 订单号
     * @return true or false
     */
    @Idem(500)
    @GetMapping("/{orderNo}/creation")
    @Log("查询订单的创建情况")
    @PreAuthorize("@S.matcher().role(@S.USER).match()")
    public Result<Boolean> orderCreation(@PathVariable String orderNo) {
        return Result.ok(
                this.createIntegralOrderService.orderCreation(orderNo)
        );
    }

    /**
     * 用户关闭未支付的积分订单
     *
     * @param integralOrderNo 积分订单号
     * @author shishuqian
     */
    @PostMapping("/close/{integralOrderNo}")
    @Log("用户关闭未支付的积分订单")
    @PreAuthorize("@S.matcher().role(@S.USER).match()")
    public Result<Void> closeUnpaidIntegralOrder(@PathVariable("integralOrderNo") @Valid @NotBlank String integralOrderNo) {
        //获取登录用户信息
        SecureUser<?> secureUser = ISecurity.userMust();
        //关闭订单
        this.closeIntegralOrderService.closeIntegralOrderPaidTimeout(
                new IntegralOrderDetailInfoBO()
                        .setIntegralOrderNo(integralOrderNo)
                        .setBuyerId(secureUser.getId())
        );
        return Result.ok();
    }


    @Log("获取超时时间")
    @GetMapping("time")
    public Result<Long> getOvertime() {
        Long time = createIntegralOrderService.getOvertime();
        return Result.ok(time);
    }

    @Log("修改超时时间")
    @PutMapping("time/{time}")
    public Result<Void> updateOvertime(@PathVariable("time") @Min(3 * 24 * 60 * 60)
                                       @Max(30 * 24 * 60 * 60) Long time) {
        createIntegralOrderService.updateOvertime(time);
        return Result.ok();
    }


    @Log("获取警告订单数")
    @PutMapping("warning")
    public Result<Long> warningIntegralOrder() {
        return Result.ok(iIntegralOrderService.warningIntegralOrder());
    }
}
