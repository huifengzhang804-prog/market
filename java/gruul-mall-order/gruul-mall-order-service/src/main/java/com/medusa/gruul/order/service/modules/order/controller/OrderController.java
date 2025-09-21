package com.medusa.gruul.order.service.modules.order.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.wechat.WechatProperties;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.api.entity.OrderPayment;
import com.medusa.gruul.order.api.entity.ShopOrder;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.service.model.dto.*;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.model.vo.*;
import com.medusa.gruul.order.service.modules.order.service.*;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


/**
 * 订单信息表 前端控制器
 *
 * @author 张治保
 * @since 2022-02-25
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderSaveService orderSaveService;
    private final OrderValidService orderValidService;
    private final QueryOrderService queryOrderService;
    private final CloseOrderService closeOrderService;
    private final CreateOrderService createOrderService;
    private final OrderExportService orderExportService;
    private final QueryOrderCallShopRpcService queryOrderCallShopRpcService;


    /**
     * 下单商品数据检查
     *
     * @return 检查结果
     */
    @PostMapping("/valid")
    @Log("下单商品数据检查")
    public Result<Void> orderValid(@RequestBody @Valid OrderValidDTO orderValid) {
        orderValid.validParam();
        orderValidService.orderValid(orderValid);
        return Result.ok();
    }

    /**
     * 订单价格 预计算接口
     *
     * @param orderShops 请求体：下单参数
     * @return 订单预算价格：总价、优惠、运费及支付价
     */
    @PostMapping("/budget")
    @Log("预算订单价格")
    @PreAuthorize(value = "@S.matcher().role(@S.USER).match()")
    public Result<OrderBudgetVO> orderBudget(@RequestBody @Valid OrderShopsDTO orderShops) {
        orderShops.setBudget(Boolean.TRUE);
        orderShops.validParam();
        return Result.ok(
                createOrderService.orderBudget(orderShops)
        );
    }


    /**
     * 创建订单并返回订单号
     *
     * @param orderShops orderShops
     * @return 订单号
     */
    @PostMapping
    @Idem(expire = 500)
    @Log("创建订单并返回订单号")
    @PreAuthorize(value = "@S.matcher().role(@S.USER).match()")
    public Result<OrderRespVO> orderCreate(@RequestBody @Valid OrderShopsDTO orderShops) {
        // 黑名单用户 提示语
        OrderError.CURRENT_USER_NONSUPPORT_BUY.trueThrow(ISecurity.userMust().getSubRoles().contains(Roles.FORBIDDEN_ORDER));
        orderShops.validParam();
        return Result.ok(
                createOrderService.create(orderShops)
        );
    }

    /**
     * 查询小程序订单的物流服务是否已经收货而程序还没有确认收货,如果是则调用确认收货提醒接口
     * @param orderNo
     * @return
     */
    @PreAuthorize(value = "@S.matcher().roles(@S.SHOP_ADMIN,@S.PLATFORM_ADMIN)" +
            ".or(@S.consumer().role(@S.SHOP_CUSTOM_ADMIN).perm('order:delivery'))" +
            ".or(@S.consumer().role(@S.PLATFORM_CUSTOM_ADMIN).perm('order:delivery')).match()")
    @GetMapping("/miniApp/deliver/confirm/{orderNo}")
    public Result<Void> miniAppDeliverConfirm(@PathVariable("orderNo") String orderNo){
        queryOrderService.miniAppDeliverConfirm(orderNo);


        return Result.ok();
    }


    /**
     * 分页查询订单
     *
     * @param queryPage 订单查询条件
     * @return 分页订单
     */
    @GetMapping
    @Log("分页查询订单")
//    @PreAuthorize(value = "@S.matcher().roles(@S.SHOP_ADMIN,@S.PLATFORM_ADMIN,"
//            + "@S.R.SUPPLIER_ADMIN,@S.R.SHOP_STORE,@S.USER)" +
//            ".or(@S.consumer().role(@S.SHOP_CUSTOM_ADMIN).perm('order:delivery'))" +
//            ".or(@S.consumer().role(@S.R.SUPPLIER_CUSTOM_ADMIN)).perm('order:delivery')"+
//            ".or(@S.consumer().role(@S.PLATFORM_CUSTOM_ADMIN).perm('order')).match()")
    @PreAuthorize("""
              @SS.platform('order')
                .shop('order:delivery')
                .otherRoles(@S.R.SHOP_STORE,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN,@S.USER)
                .match()
            """)
    public Result<IPage<Order>> orderPage(OrderQueryDTO queryPage) {
        ISecurity.match()
                .ifUser(secureUser -> queryPage.setBuyerId(secureUser.getId()))
                .ifAnyShopAdmin(
                        secureUser -> queryPage.setShopIds(Set.of(secureUser.getShopId()))
                                .setIsPriority(Boolean.TRUE)
                )
                .ifAnySupplierAdmin(
                        secureUser -> queryPage.setSupplierId(secureUser.getShopId())
                                .setSellType(SellType.CONSIGNMENT)
                                .setIsPriority(Boolean.TRUE)
                );
        return Result.ok(queryOrderCallShopRpcService.orderPage(false, queryPage));
    }

    /**
     * 订单数统计
     *
     * @param query 订单数统计过滤参数
     * @return 订单数量统计结果
     */
    @PostMapping("/count")
    public Result<OrderCountVO> orderCount(@RequestBody OrderCountQueryDTO query) {
        return Result.ok(queryOrderService.orderCount(query));
    }


    /**
     * 订单列表导出
     *
     * @param queryPage 订单查询条件
     * @return 导出记录id
     */
    @PostMapping("/export")
    @Log("导出订单列表")
    @PreAuthorize("""
            @SS.platform('order')
                .shop('order:delivery')
                .otherRoles(@S.R.SHOP_STORE,@S.R.SUPPLIER_ADMIN,@S.USER)
                .match()
            """)
    public Result<Long> export(@RequestBody OrderQueryDTO queryPage) {

        Long exportRecordId = orderExportService.export(queryPage, ExportDataType.USER_ORDER);
        return Result.ok(exportRecordId);
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
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    public Result<Boolean> orderCreation(@PathVariable String orderNo) {
        return Result.ok(
                queryOrderService.orderCreation(orderNo)
        );
    }

    /**
     * 根据订单号查询订单详情
     *
     * @param orderDetailQuery 订单详情查询条件
     * @return 订单详情
     */
    @GetMapping("/{orderNo}")
    @Log("根据订单号查询订单详情")
    @PreAuthorize("""
            @SS.platform('order','order:afs')
                .shop('order:delivery','order:sale')
                .otherRoles(@S.R.SHOP_STORE,@S.R.SUPPLIER_ADMIN,@S.USER)
                .match()
            """)
    public Result<Order> getOrderByNo(@Valid OrderDetailQueryDTO orderDetailQuery) {
        return Result.ok(
                queryOrderService.orderDetail(orderDetailQuery).getOrNull()
        );
    }

    /**
     * 根据订单号 店铺订单号查询店铺订单详情
     *
     * @param orderNo     订单号
     * @param shopOrderNo 店铺订单号
     * @return 店铺订单详情
     */
    @Log("根据订单号 店铺订单号查询店铺订单详情")
    @GetMapping("/{orderNo}/shopOrder/{shopOrderNo}")
    @PreAuthorize("""
            @SS.platform('order','order:afs')
                .shop('order:delivery')
                .otherRoles(@S.R.SUPPLIER_ADMIN,@S.USER)
                .match()
            """)
    public Result<ShopOrder> getShopOrderByNo(@PathVariable String orderNo, @PathVariable String shopOrderNo) {
        return Result.ok(
                queryOrderService.getShopOrderByNo(orderNo, shopOrderNo).getOrNull()
        );
    }

    /**
     * 根据订单号与item id 查询 订单商品项
     */
    @Log("根据订单号与item id 查询 订单商品项")
    @GetMapping("/{orderNo}/item/{itemId}")
    @PreAuthorize("""
            @SS.platform('order','order:afs')
                .shop('order:delivery')
                .otherRoles(@S.USER)
                .match()
            """)
    public Result<ShopOrderItem> getShopOrderItem(@PathVariable String orderNo, @PathVariable Long itemId) {
        return Result.ok(
                queryOrderService.getShopOrderItem(orderNo, itemId).getOrNull()
        );
    }

    /**
     * 店铺关闭未支付订单
     */
    @Log("店铺关闭未支付订单")
    @PreAuthorize("""
                @SS.platform('order')
                .shop('order:delivery')
                .otherRoles(@S.R.SUPPLIER_ADMIN)
                .match()
            """)
    @PutMapping("/{orderNo}/shopOrder/{shopOrderNo}/close")
    public Result<Void> shopCloseOrder(@PathVariable String orderNo, @PathVariable String shopOrderNo) {
        closeOrderService.shopCloseOrder(orderNo, shopOrderNo);
        return Result.ok();
    }

    /**
     * 获取未支付的订单支付信息
     *
     * @param orderNo 订单号
     * @return 未支付的订单支付信息
     */
    @GetMapping("/{orderNo}/payment")
    @Log("获取未支付的订单支付信息")
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    public Result<OrderPayment> getUnpaidOrderPayment(@PathVariable String orderNo) {
        return Result.ok(
                queryOrderService.getUnpaidOrderPayment(orderNo)
        );
    }


    /**
     * 关闭订单
     *
     * @param orderNo 订单号
     */
    @PutMapping("/{orderNo}/close")
    @Log("关闭订单")
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    public Result<Void> closeOrder(@PathVariable String orderNo) {
        closeOrderService.updateOrderStatus(orderNo);
        return Result.ok();
    }

    /**
     * 用户个人订单统计
     */
    @GetMapping("/my/count")
    @Log("用户个人订单统计")
    @PreAuthorize("@S.matcher().role(@S.R.USER).match()")
    public Result<BuyerOrderCountVO> orderCount() {
        return Result.ok(
                queryOrderService.buyerOrderCount()
        );
    }


    /**
     * 订单批量备注
     *
     * @param orderRemark 订单备注参数
     */
    @Idem(500)
    @Log("批量备注")
    @PutMapping("/remark/batch")
    @PreAuthorize("""
            @SS.platform('order')
                .shop('order:delivery')
                .otherRoles(@S.R.SUPPLIER_ADMIN)
                .match()
            """)
    public Result<Void> orderBatchRemark(@RequestBody @Valid OrderRemarkDTO orderRemark) {
        orderSaveService.orderBatchRemark(orderRemark);
        return Result.ok();
    }

    /**
     * 根据订单号，查询自营店铺订单和自营供货商订单
     *
     * @param dto 订单参数
     * @return 返回自营店铺订单和自营供货商订单
     */
    @Log("根据订单号，查询自营店铺订单和自营供货商订单")
    @PostMapping("/getPlatFormOrder")
    @PreAuthorize("""
                    @S.matcher()
                    .any(@S.ROLES,@S.PLATFORM_ADMIN,@S.R.SUPER_CUSTOM_ADMIN)
                    .match()
            """)
    public Result<OrderPlatFormDeliveryVO> getPlatFormDeliveryOrder(@RequestBody OrderPlatFormDeliveryDTO dto) {
        return Result.ok(queryOrderCallShopRpcService.getPlatFormDeliveryOrder(dto));
    }
}