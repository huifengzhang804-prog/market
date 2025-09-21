package com.medusa.gruul.addon.supplier.modules.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.supplier.model.dto.*;
import com.medusa.gruul.addon.supplier.model.enums.InvoiceStatus;
import com.medusa.gruul.addon.supplier.model.enums.OrderStatus;
import com.medusa.gruul.addon.supplier.model.vo.OrderCreateVO;
import com.medusa.gruul.addon.supplier.model.vo.OrderStorageVO;
import com.medusa.gruul.addon.supplier.model.vo.PublishProductGetVO;
import com.medusa.gruul.addon.supplier.model.vo.PublishProductVO;
import com.medusa.gruul.addon.supplier.modules.order.service.PurchaseOrderExportService;
import com.medusa.gruul.addon.supplier.modules.order.service.SupplierOrderCreateService;
import com.medusa.gruul.addon.supplier.modules.order.service.SupplierOrderHandleService;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrder;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrderPackage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.o.Final;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 供应商订单控制器
 *
 * @author 张治保
 */
@RestController
@RequestMapping("/supplier/order")
@RequiredArgsConstructor
@Validated
@PreAuthorize("@S.shopPerm('goods:purchase')")

public class SupplierOrderController {

    private final SupplierOrderCreateService supplierOrderCreateService;
    private final SupplierOrderHandleService supplierOrderHandleService;
    private final PurchaseOrderExportService purchaseOrderExportService;

    /**
     * 供应商采购订单创建
     *
     * @param orderCreate 供应商采购订单dto
     * @return 供应商采购订单VO
     */
    @PostMapping
    @Log("供应商订单创建")
    @Idem
    public Result<OrderCreateVO> createOrder(@RequestBody @Valid OrderCreateDTO orderCreate) {
        return Result.ok(
                supplierOrderCreateService.createOrder(orderCreate)
        );
    }

    /**
     * 轮询查询订单创建结果
     *
     * @param mainNo 订单号
     * @return 是否成功
     */
    @GetMapping("/creation/{mainNo}")
    @Log("轮训查询供应商订单创建结果")
    @Idem(100)
    public Result<Boolean> createResult(@PathVariable String mainNo) {
        return Result.ok(
                supplierOrderCreateService.createResult(mainNo)
        );
    }

    /**
     * 分页查询供应商订单
     *
     * @param query 查询条件
     * @return 分页查询结果
     */

    @GetMapping
    @Log("分页查询供应商订单")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN,@S.PLATFORM_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'order:purchase'))
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'goods:purchase'))
            .match()
            """)
    public Result<IPage<SupplierOrder>> orderPage(OrderQueryPageDTO query) {
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> query.setShopId(secureUser.getShopId()).setNeedSupplier(Boolean.TRUE))
                .when(secureUser -> query.setSupplierId(secureUser.getShopId()), Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN);
        if (Objects.nonNull(query.getInvoiceStatus())) {
            query.getInvoiceStatusList().add(query.getInvoiceStatus());
            if (InvoiceStatus.INVOICE_NOT_START.equals(query.getInvoiceStatus())) {
                query.getInvoiceStatusList().add(InvoiceStatus.CLIENT_CANCEL_REQUEST);
            }
        }
        return Result.ok(
                supplierOrderHandleService.orderPage(query)
        );
    }

    /**
     * 供应商订单导出
     *
     * @param query 条件query
     * @return exportDataId
     */
    @PostMapping("/export")
    @Log("供应商订单导出")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'order:purchase'))
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'goods:purchase'))
            .match()
            """)
    public Result<Long> export(@RequestBody OrderQueryPageDTO query) {
        Long exportDataId = purchaseOrderExportService.export(query, ExportDataType.PURCHASE_ORDER);
        return Result.ok(exportDataId);
    }

    /**
     * 查询供应商订单详情
     *
     * @param orderNo 订单号
     * @return 订单详情
     */
    @Log("查询供应商订单详情")
    @GetMapping("/{orderNo}")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'order:purchase'))
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'goods:purchase'))
            .match()
            """)
    public Result<SupplierOrder> order(@PathVariable String orderNo) {
        OrderDetailQueryDTO query = new OrderDetailQueryDTO()
                .setNeedSupplier(Boolean.TRUE);
        query.setOrderNo(orderNo);
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> query.setShopId(secureUser.getShopId()))
                .when(secureUser -> query.setNeedSupplier(Boolean.FALSE).setSupplierId(secureUser.getShopId()), Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN);
        return Result.ok(
                supplierOrderHandleService.order(query)
        );
    }

    /**
     * 供应商订单支付
     *
     * @param orderPay 订单支付参数
     * @return void
     */
    @Log("供应商订单支付")
    @PutMapping("/pay")
    public Result<Void> orderPay(@RequestBody @Valid OrderPayDTO orderPay) {
        supplierOrderHandleService.orderPay(ISecurity.userMust().getShopId(), orderPay);
        return Result.ok();
    }

    /**
     * 供应商订单支付审核
     *
     * @param audit 订单支付审核结果
     * @return void
     */
    @Log("供应商订单支付审核")
    @PreAuthorize("@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN).match()")
    @PutMapping("/pay/audit")
    public Result<Void> orderPayAudit(@RequestBody @Valid OrderPayAuditDTO audit) {
        supplierOrderHandleService.orderPayAudit(ISecurity.userMust().getShopId(), audit);
        return Result.ok();
    }

    /**
     * 供应商订单关闭
     *
     * @param orderNo 订单号
     * @return void
     */
    @Log("供应商订单关闭")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'goods:purchase'))
            .match()
            """)
    @PutMapping("/close/{orderNo}")
    public Result<Void> closeOrder(@PathVariable String orderNo) {
        OrderMatchQueryDTO orderMatch = new OrderMatchQueryDTO()
                .setOrderNo(orderNo);
        Final<OrderStatus> statusBox = new Final<>(OrderStatus.SELLER_CLOSED);
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> {
                    orderMatch.setShopId(secureUser.getShopId());
                    statusBox.set(OrderStatus.BUYER_CLOSED);
                })
                .when(secureUser -> orderMatch.setSupplierId(secureUser.getShopId()), Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN);
        supplierOrderHandleService.closeOrder(orderMatch, statusBox.get());
        return Result.ok();
    }

    /**
     * 供应商订单发货
     *
     * @param delivery 订单发货参数
     * @return void
     */
    @Log("供应商订单发货")
    @PreAuthorize("@S.matcher().eq(@S.ROLES,@S.R.SUPPLIER_ADMIN).match()")
    @PutMapping("/delivery")
    @Idem(1000)
    public Result<Void> orderDelivery(@RequestBody @Valid OrderDeliveryDTO delivery) {
        supplierOrderHandleService.orderDelivery(ISecurity.userMust().getShopId(), delivery);
        return Result.ok();
    }

    /**
     * 供应商订单批量发货
     *
     * @param deliveries 订单发货参数
     * @return void
     */
    @Log("供应商订单批量发货")
    @PreAuthorize("@S.matcher().eq(@S.ROLES,@S.R.SUPPLIER_ADMIN).match()")
    @PutMapping("/delivery/batch")
    @Idem(1000)
    public Result<Void> orderDeliveryBatch(@RequestBody @Valid @NotNull @Size(min = 1) Set<OrderDeliveryDTO> deliveries) {
        supplierOrderHandleService.orderDeliveryBatch(ISecurity.userMust().getShopId(), deliveries);
        return Result.ok();
    }

    /**
     * 查询已发货的包裹信息列表
     *
     * @param orderNo 订单号
     * @return 包裹信息列表
     */
    @Log("查询已发货的包裹信息列表")
    @GetMapping("/delivery/{orderNo}")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'order:purchase'))
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'goods:purchase'))
            .match()
            """)
    public Result<List<SupplierOrderPackage>> deliveryPackages(@PathVariable String orderNo) {
        OrderMatchQueryDTO deliveryQuery = new OrderMatchQueryDTO()
                .setOrderNo(orderNo);
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> deliveryQuery.setShopId(secureUser.getShopId()))
                .when(secureUser -> deliveryQuery.setSupplierId(secureUser.getShopId()), Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN);
        return Result.ok(supplierOrderHandleService.deliveryPackages(deliveryQuery));
    }

    /**
     * 查询供应商订单入库详情
     *
     * @param orderNo 订单no
     * @return 订单数据
     */
    @Log("查询供应商订单入库详情")
    @GetMapping("/storage/{orderNo}")
    @PreAuthorize("""
            @S.matcher()
            .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.PLATFORM_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'order:purchase'))
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'goods:purchase'))
            .match()
            """)
    public Result<OrderStorageVO> storageQuery(@PathVariable String orderNo) {
        OrderMatchQueryDTO query = new OrderMatchQueryDTO()
                .setOrderNo(orderNo);
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> query.setShopId(secureUser.getShopId()))
                .when(secureUser -> query.setSupplierId(secureUser.getShopId()), Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN);
        return Result.ok(
                supplierOrderHandleService.storageQuery(query)
        );
    }

    @Idem
    @Log("供应商订单入库")
    @PutMapping("/storage")
    public Result<Void> storage(@RequestBody @Valid OrderStorageDTO storage) {
        supplierOrderHandleService.storage(
                storage.setShopId(ISecurity.userMust().getShopId())
        );
        return Result.ok();
    }

    /**
     * 订单完成
     *
     * @param orderNo 订单号
     */
    @Log("订单完成")
    @PutMapping("/complete/{orderNo}")
    public Result<Void> orderComplete(@PathVariable String orderNo) {
        supplierOrderHandleService.orderComplete(
                new OrderMatchQueryDTO().setOrderNo(orderNo).setShopId(ISecurity.userMust().getShopId())
        );
        return Result.ok();
    }


    @Log("供应商订单批量备注")
    @PutMapping("/remark/batch")
    @PreAuthorize("@S.matcher().eq(@S.ROLES,@S.R.SUPPLIER_ADMIN).match()")
    public Result<Void> orderRemarkBatch(@RequestBody @Valid OrderRemarkBatchDTO remark) {
        supplierOrderHandleService.orderRemarkBatch(remark.setSupplierId(ISecurity.userMust().getShopId()));
        return Result.ok();
    }

    /**
     * 分页查询待发布商品列表
     *
     * @param query 条件查询
     * @return IPage<PublishProductVO>
     */

    @Log("分页查询待发布商品列表")
    @GetMapping("/publish")
    public Result<IPage<PublishProductVO>> publishPage(@Valid PublishProductQueryDTO query) {
        query.setShopId(ISecurity.userMust().getShopId());
        return Result.ok(
                supplierOrderHandleService.publishPage(query)
        );
    }

    /**
     * 查询待发布商品信息
     *
     * @param publishProductGet 待发布商品查询信息
     * @return 待发布商品信息
     */
    @Log("查询待发布商品信息")
    @GetMapping("/publish/product/{productId}")
    public Result<PublishProductGetVO> publishProductGet(@Valid PublishProductGetDTO publishProductGet) {
        publishProductGet.setShopId(ISecurity.userMust().getShopId());
        return Result.ok(
                supplierOrderHandleService.publishProductGet(publishProductGet)
        );
    }


}
