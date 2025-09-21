package com.medusa.gruul.addon.supplier.modules.order.service.impl;

import com.medusa.gruul.addon.supplier.model.dto.OrderQueryPageDTO;
import com.medusa.gruul.addon.supplier.modules.goods.service.QuerySupplierGoodsService;
import com.medusa.gruul.addon.supplier.modules.order.service.PurchaseOrderExportService;
import com.medusa.gruul.addon.supplier.modules.order.service.SupplierOrderHandleService;
import com.medusa.gruul.addon.supplier.modules.order.task.PurchaseOrderExportTask;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;


/**
 * 采购订单导出服务实现类
 *
 * @author jipeng
 * @since 2023/7/19
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PurchaseOrderExportServiceImpl implements PurchaseOrderExportService {


    private final ShopRpcService shopRpcService;

    private final Executor globalExecutor;

    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;

    private final DataExportRecordRpcService dataExportRecordRpcService;

    private final SupplierOrderHandleService supplierOrderHandleService;

    private final UaaRpcService uaaRpcService;

    private final QuerySupplierGoodsService querySupplierGoodsService;


    /**
     * 导出采购订单
     *
     * @param query         查询条件
     * @param purchaseOrder 导出数据类型
     * @return 导出记录id
     */
    @Override
    public Long export(OrderQueryPageDTO query, ExportDataType purchaseOrder) {
        DataExportRecordDTO dto = new DataExportRecordDTO();
        dto.setExportUserId(ISecurity.userMust().getId()).setDataType(purchaseOrder)
                .setShopId(ISystem.shopIdMust())
                .setUserPhone(ISecurity.userMust().getMobile());
        //RPC保存导出记录
        Long exportRecordId = dataExportRecordRpcService.saveExportRecord(dto);
        query.setExport(Boolean.TRUE);
        ISecurity.match()
                .ifAnyShopAdmin(secureUser -> query.setShopId(secureUser.getShopId()).setNeedSupplier(Boolean.TRUE))
                .when(secureUser -> query.setSupplierId(secureUser.getShopId()), Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN);
        PurchaseOrderExportTask task=new PurchaseOrderExportTask(exportRecordId,supplierOrderHandleService,
                query,uaaRpcService,shopRpcService,
                dataExportRecordRpcService,pigeonChatStatisticsRpcService,
                  querySupplierGoodsService);
        globalExecutor.execute(task);
        return exportRecordId;
    }
}
