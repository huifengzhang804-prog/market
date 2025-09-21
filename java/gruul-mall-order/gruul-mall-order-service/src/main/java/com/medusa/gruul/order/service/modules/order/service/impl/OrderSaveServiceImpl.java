package com.medusa.gruul.order.service.modules.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.order.api.constant.OrderConstant;
import com.medusa.gruul.order.api.entity.*;
import com.medusa.gruul.order.service.model.dto.OrderRemarkDTO;
import com.medusa.gruul.order.service.modules.order.service.OrderSaveService;
import com.medusa.gruul.order.service.mp.service.*;
import io.vavr.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 保存订单信息到数据库 流程服务实现类
 *
 * @author 张治保
 * date 2022/6/14
 */
@Service
@RequiredArgsConstructor
public class OrderSaveServiceImpl implements OrderSaveService {

    private final IOrderService orderService;
    private final IShopOrderService shopOrderService;
    private final IShopOrderItemService shopOrderItemService;
    private final IOrderDiscountService orderDiscountService;
    private final IOrderDiscountItemService orderDiscountItemService;
    private final IOrderPaymentService orderPaymentService;
    private final IOrderReceiverService orderReceiverService;

    @Override
    public void saveOrder(Order order) {
        SystemCode.DATA_ADD_FAILED.falseThrow(orderService.save(order));
    }

    @Override
    public void saveOrderBatch(List<Order> orders) {
        if (CollUtil.isEmpty(orders)) {
            return;
        }
        orderService.saveBatch(orders);
    }

    @Override
    public void saveOrderReceiver(OrderReceiver receiver) {
        SystemCode.DATA_ADD_FAILED.falseThrow(orderReceiverService.save(receiver));
    }

    @Override
    public void saveOrderReceivers(List<OrderReceiver> orderReceivers) {
        if (CollUtil.isEmpty(orderReceivers)) {
            return;
        }
        orderReceiverService.saveBatch(orderReceivers);

    }

    @Override
    public void saveOrderDiscounts(List<OrderDiscount> orderDiscounts) {
        if (CollUtil.isEmpty(orderDiscounts)) {
            return;
        }
        SystemCode.DATA_ADD_FAILED.falseThrow(orderDiscountService.saveBatch(orderDiscounts));
    }

    @Override
    public void saveOrderDiscountItems(List<OrderDiscountItem> orderDiscountItems) {
        if (CollUtil.isEmpty(orderDiscountItems)) {
            return;
        }
        SystemCode.DATA_ADD_FAILED.falseThrow(orderDiscountItemService.saveBatch(orderDiscountItems));
    }

    @Override
    public void saveShopOrders(List<ShopOrder> shopOrders) {
        if (CollUtil.isEmpty(shopOrders)) {
            return;
        }
        SystemCode.DATA_ADD_FAILED.falseThrow(shopOrderService.saveBatch(shopOrders));
    }

    @Override
    public void saveShopOrderItems(List<ShopOrderItem> shopOrderItems) {
        if (CollUtil.isEmpty(shopOrderItems)) {
            return;
        }
        SystemCode.DATA_ADD_FAILED.falseThrow(shopOrderItemService.saveBatch(shopOrderItems));
    }


    @Override
    public void saveOrderPayment(OrderPayment orderPayment) {
        SystemCode.DATA_ADD_FAILED.falseThrow(orderPaymentService.save(orderPayment));
    }

    @Override
    public void saveOrderPayments(List<OrderPayment> orderPayments) {
        if (CollUtil.isEmpty(orderPayments)) {
            return;
        }
        orderPaymentService.saveBatch(orderPayments);
    }

    @Override
    public void orderBatchRemark(OrderRemarkDTO orderRemark) {
        String remark = StrUtil.trim(orderRemark.getRemark());
        ISecurity.match()
                .ifAnySuperAdmin(
                        secureUser -> TenantShop.disable(
                                () -> shopOrderService.lambdaUpdate()
                                        .in(ShopOrder::getNo, orderRemark.getNos())
                                        .setSql(SqlHelper.renderJsonSetSql("remark", Tuple.of(OrderConstant.PLATFORM_REMARK_KEY, remark)))
                                        .update()
                        )
                )
                .ifAnyShopAdmin(
                        secureUser -> TenantShop.disable(
                                () -> shopOrderService.lambdaUpdate()
                                        .in(ShopOrder::getNo, orderRemark.getNos())
                                        .eq(ShopOrder::getShopId, secureUser.getShopId())
                                        .setSql(SqlHelper.renderJsonSetSql("remark", Tuple.of(OrderConstant.SHOP_REMARK_KEY, remark)))
                                        .update()
                        )
                )
                .ifAnySupplierAdmin(
                        secureUser ->
                                TenantShop.disable(
                                        () -> shopOrderService.lambdaUpdate()
                                                .in(ShopOrder::getNo, orderRemark.getNos())
                                                .exists("""
                                                        SELECT item.id FROM t_shop_order_item AS item WHERE item.shop_id = t_shop_order.shop_id
                                                        AND item.supplier_id={0}
                                                        AND item.sell_type={1}
                                                        """, secureUser.getShopId(), SellType.CONSIGNMENT.getValue()
                                                )
                                                .setSql(SqlHelper.renderJsonSetSql("remark", Tuple.of(OrderConstant.SUPPLIER_REMARK_KEY, remark)))
                                                .update()
                                )
                );
    }
}
