package com.medusa.gruul.addon.supplier.modules.order.handler;

import com.medusa.gruul.addon.supplier.model.bo.OrderExtra;
import com.medusa.gruul.addon.supplier.model.bo.Receiver;
import com.medusa.gruul.addon.supplier.model.dto.OrderDeliveryDTO;
import com.medusa.gruul.addon.supplier.model.enums.PackageStatus;
import com.medusa.gruul.addon.supplier.modules.order.service.SupplierOrderHandleService;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrderPackage;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierOrderPackageService;
import com.medusa.gruul.global.model.express.ExpressCompanyDTO;
import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.order.api.enums.DeliverType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author 张治保
 * date 2023/7/25
 */
@Component
@RequiredArgsConstructor
@DeliverTypeHandler(DeliverType.WITHOUT)
public class WithoutDeliverHandler extends AbstractDeliverHandler {

    private final SupplierOrderHandleService supplierOrderHandleService;
    private final ISupplierOrderPackageService supplierOrderPackageService;

    @Override
    public SupplierOrderPackage handleIt(Long supplierId, OrderDeliveryDTO orderDelivery, OrderExtra orderExtra) {
        Receiver receiver = orderExtra.getReceiver();
        SupplierOrderPackage orderPackage = new SupplierOrderPackage()
                .setSupplierId(supplierId)
                .setOrderNo(orderDelivery.getOrderNo())
                .setStatus(PackageStatus.WAITING_FOR_RECEIVE)
                .setType(DeliverType.WITHOUT)
                .setExpress(new ExpressCompanyDTO())
                .setReceiver(new UserAddressDTO()
                        .setName(receiver.getName())
                        .setMobile(receiver.getMobile())
                        .setAddress(receiver.getAddress()));
        supplierOrderPackageService.save(orderPackage);
        supplierOrderHandleService.splitDeliveryItems(orderDelivery.getOrderNo(), orderPackage.getId(), orderDelivery.getItems());
        return orderPackage;
    }


}
