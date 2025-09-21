package com.medusa.gruul.addon.supplier.modules.order.handler;

import com.medusa.gruul.addon.supplier.addon.SupplierLogisticsAddonSupport;
import com.medusa.gruul.addon.supplier.model.bo.OrderExtra;
import com.medusa.gruul.addon.supplier.model.dto.OrderDeliveryDTO;
import com.medusa.gruul.addon.supplier.model.enums.PackageStatus;
import com.medusa.gruul.addon.supplier.modules.order.service.SupplierOrderHandleService;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrderPackage;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierOrderPackageService;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.order.api.addon.freight.SendDeliveryDTO;
import com.medusa.gruul.order.api.enums.DeliverType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author 张治保
 * date 2023/7/25
 */
@Component
@RequiredArgsConstructor
@DeliverTypeHandler(DeliverType.PRINT_EXPRESS)
public class PrintExpressDeliverHandler extends AbstractDeliverHandler {

    private final SupplierLogisticsAddonSupport supplierLogisticsAddonSupport;
    private final SupplierOrderHandleService supplierOrderHandleService;
    private final ISupplierOrderPackageService supplierOrderPackageService;

    @Override
    public SupplierOrderPackage handleIt(Long supplierId, OrderDeliveryDTO orderDelivery, OrderExtra orderExtra) {
        SupplierOrderPackage orderPackage = new SupplierOrderPackage()
                .setSupplierId(supplierId)
                .setOrderNo(orderDelivery.getOrderNo())
                .setStatus(PackageStatus.WAITING_FOR_RECEIVE)
                .setType(DeliverType.PRINT_EXPRESS)
                .setExpress(orderDelivery.getExpressCompany())
                .setReceiver(orderDelivery.getReceiver());
        orderPackage.setId(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(orderPackage).longValue());
        BigDecimal weight = supplierOrderHandleService.splitDeliveryItems(orderDelivery.getOrderNo(), orderPackage.getId(), orderDelivery.getItems());
        String expressNo = supplierLogisticsAddonSupport.printDeliverGoods(
                new SendDeliveryDTO()
                        .setSender(orderDelivery.getSender())
                        .setReceiver(orderDelivery.getReceiver())
                        .setRemark(orderDelivery.getRemark())
                        .setDropDeliver(false)
                        .setExpressCompanyCode(orderPackage.getExpress().getExpressCompanyCode())
                        .setCargo(orderDelivery.getCargo())
                        .setNo(orderDelivery.getOrderNo())
                        .setWeight(weight)
        );
        orderPackage.getExpress().setExpressNo(expressNo);
        supplierOrderPackageService.save(orderPackage);
        return orderPackage;
    }


}
