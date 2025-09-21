package com.medusa.gruul.addon.supplier.modules.order.handler;

import com.medusa.gruul.addon.supplier.model.bo.OrderExtra;
import com.medusa.gruul.addon.supplier.model.dto.OrderDeliveryDTO;
import com.medusa.gruul.addon.supplier.model.enums.PackageStatus;
import com.medusa.gruul.addon.supplier.modules.order.service.SupplierOrderHandleService;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrderPackage;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierOrderPackageService;
import com.medusa.gruul.order.api.enums.DeliverType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author 张治保
 * date 2023/7/25
 */
@Component
@RequiredArgsConstructor
@DeliverTypeHandler(DeliverType.EXPRESS)
public class ExpressDeliverHandler extends AbstractDeliverHandler {

    private final SupplierOrderHandleService supplierOrderHandleService;
    private final ISupplierOrderPackageService supplierOrderPackageService;

    @Override
    public SupplierOrderPackage handleIt(Long supplierId, OrderDeliveryDTO orderDelivery, OrderExtra orderExtra) {
        SupplierOrderPackage orderPackage = new SupplierOrderPackage()
                .setSupplierId(supplierId)
                .setOrderNo(orderDelivery.getOrderNo())
                .setStatus(PackageStatus.WAITING_FOR_RECEIVE)
                .setType(DeliverType.EXPRESS)
                .setExpress(orderDelivery.getExpressCompany())
                .setReceiver(orderDelivery.getReceiver());
        supplierOrderPackageService.save(orderPackage);
        supplierOrderHandleService.splitDeliveryItems(orderDelivery.getOrderNo(), orderPackage.getId(), orderDelivery.getItems());
        return orderPackage;
    }


}
