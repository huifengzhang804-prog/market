package com.medusa.gruul.storage.service.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.storage.service.model.dto.StorageManagementOrderDTO;
import com.medusa.gruul.storage.service.model.param.StorageManagementOrderParam;
import com.medusa.gruul.storage.service.mp.entity.StorageManagementOrder;
import com.medusa.gruul.storage.service.mp.entity.StorageManagementOrderItem;
import com.medusa.gruul.storage.service.service.EditStorageManagementOrderService;
import com.medusa.gruul.storage.service.service.QueryStorageManagementOrderService;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仓储管理控制层
 *
 * @author xiaoq
 * @Description StorageManagementOrderController.java
 * @date 2023-07-25 14:38
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/storage/management/order")
@PreAuthorize("""
        		@S.matcher().any(@S.ROLES,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN,@S.SHOP_ADMIN).
        		or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).any(@S.PERMS,'inventory:count')).match()
        """)
public class StorageManagementOrderController {

    private final QueryStorageManagementOrderService queryStorageManagementOrderService;

    private final EditStorageManagementOrderService editStorageManagementOrderService;


    /**
     * 仓储管理订单创建
     *
     * @param storageManagementOrder 仓储管理订单DTO
     * @return 订单号
     */
    @Idem(1000)
    @Log("仓储管理订单创建")
    @PostMapping("create")
    public Result<String> createStorageManagementOrder(
            @RequestBody @Validated StorageManagementOrderDTO storageManagementOrder) {
        Set<Long> productIds = storageManagementOrder.getStorageManagementOrderItems().stream()
                .map(StorageManagementOrderItem::getProductId).collect(Collectors.toSet());
        return Result.ok(
                editStorageManagementOrderService.createStorageManagementOrder(storageManagementOrder, productIds));
    }


    /**
     * 仓储管理订单编辑
     *
     * @param storageManagementOrder 仓储管理订单DTO
     * @return Result<Void>
     */
    @Idem(1000)
    @Log("仓储管理订单编辑")
    @PostMapping("edit")
    public Result<Void> editStorageManagementOrder(
            @RequestBody @Validated StorageManagementOrderDTO storageManagementOrder) {
        Set<Long> productIds = storageManagementOrder.getStorageManagementOrderItems().stream()
                .map(StorageManagementOrderItem::getProductId).collect(Collectors.toSet());
        editStorageManagementOrderService.editStorageManagementOrder(storageManagementOrder, productIds);
        return Result.ok();
    }

    /**
     * 仓储管理订单取消
     *
     * @param id 仓储管理订单id
     * @return Result<Void>
     */
    @Log("仓储管理订单取消")
    @PutMapping("cancel/{id}")
    public Result<Void> cancelStorageManagementOrder(@PathVariable("id") @NotNull Long id) {
        editStorageManagementOrderService.cancelStorageManagementOrder(id);
        return Result.ok();
    }


    /**
     * 仓储管理订单完成
     *
     * @param id 仓储管理订单id
     * @return Result<Void>
     */
    @Idem(1000)
    @Log("仓储管理订单完成")
    @PutMapping("complete/{id}")
    public Result<Void> completeStorageManagementOrder(@PathVariable("id") @NotNull Long id) {
        editStorageManagementOrderService.completeStorageManagementOrder(id);
        return Result.ok();
    }


    /**
     * 获取仓储管理订单列表
     *
     * @param storageManagementOrderParam 查询param
     * @return Ipage<StorageManagementOrder>
     */
    @Log("获取仓储管理订单列表")
    @GetMapping("page")
    public Result<IPage<StorageManagementOrder>> getStorageManagementOrderList(
            StorageManagementOrderParam storageManagementOrderParam) {
        return Result.ok(queryStorageManagementOrderService.getStorageManagementOrderList(storageManagementOrderParam));
    }


    /**
     * 获取仓储管理订单详情
     *
     * @param id 获取仓储管理订单id
     * @return 库存管理详情(出入库, 盘点)
     */
    @Log("获取仓储管理订单详情")
    @GetMapping("get/{id}")
    public Result<StorageManagementOrder> getStorageManagementOrderDetail(@PathVariable("id") @NotNull Long id) {
        return Result.ok(queryStorageManagementOrderService.getStorageManagementOrderDetail(id));
    }


}
