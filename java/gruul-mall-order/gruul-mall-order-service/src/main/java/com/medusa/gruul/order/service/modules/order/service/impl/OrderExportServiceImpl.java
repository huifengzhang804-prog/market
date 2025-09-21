package com.medusa.gruul.order.service.modules.order.service.impl;

import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.order.service.model.dto.OrderQueryDTO;
import com.medusa.gruul.order.service.modules.order.service.OrderExportService;
import com.medusa.gruul.order.service.modules.order.service.QueryOrderCallShopRpcService;
import com.medusa.gruul.order.service.modules.order.task.OrderExportTask;
import com.medusa.gruul.order.service.mp.service.IOrderDiscountService;
import com.medusa.gruul.overview.api.enums.ExportDataType;
import com.medusa.gruul.overview.api.model.DataExportRecordDTO;
import com.medusa.gruul.overview.api.rpc.DataExportRecordRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.Executor;

/**
 * 订单导出服务实现类
 *
 * @author jipeng
 * @see com.medusa.gruul.order.service.modules.order.service.impl
 * @since 2024/1/8 10:19
 */
@Service
@Slf4j
public class OrderExportServiceImpl implements OrderExportService {

    @Resource
    private QueryOrderCallShopRpcService queryOrderCallShopRpcService;
    @Resource
    private DataExportRecordRpcService dataExportRecordRpcService;
    @Resource
    private Executor globalExecutor;
    @Resource
    private IOrderDiscountService orderDiscountService;
    @Resource
    private PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;

    @Resource
    private UaaRpcService uaaRpcService;

    public Long export(OrderQueryDTO queryPage, ExportDataType exportDataType) {
        if (!ISecurity.anyRole(Roles.USER)) {
            queryPage.setIsPriority(Boolean.TRUE);
        }
        ISecurity.match()
                .ifUser(secureUser -> queryPage.setBuyerId(secureUser.getId()))
                .ifAnyShopAdmin(secureUser -> queryPage.setShopIds(Set.of(secureUser.getShopId())))
                .ifAnySupplierAdmin(
                        secureUser -> queryPage.setSupplierId(secureUser.getShopId())
                                .setSellType(SellType.CONSIGNMENT)
                );
        DataExportRecordDTO dto = new DataExportRecordDTO();
        dto.setExportUserId(ISecurity.userMust().getId()).setDataType(exportDataType)
                .setShopId(ISystem.shopIdMust())
                .setUserPhone(ISecurity.userMust().getMobile());
        //RPC保存导出记录
        Long exportRecordId = dataExportRecordRpcService.saveExportRecord(dto);
        OrderExportTask orderExportTask = new OrderExportTask(exportRecordId,
                dataExportRecordRpcService, queryOrderCallShopRpcService, queryPage,
                uaaRpcService, orderDiscountService, pigeonChatStatisticsRpcService);

        globalExecutor.execute(orderExportTask);
        return exportRecordId;
    }

}
