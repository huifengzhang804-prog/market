package com.medusa.gruul.addon.supplier.modules.order.handler;


import com.medusa.gruul.addon.supplier.model.bo.OrderExtra;
import com.medusa.gruul.addon.supplier.model.dto.OrderDeliveryDTO;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrder;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrderPackage;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.web.handler.Handler;

/**
 * @author 张治保
 * date 2022/7/28
 */
public abstract class AbstractDeliverHandler implements Handler<Object> {

    @Override
    public SupplierOrderPackage handle(Object... params) {
        if (this.hasErrorParam(params, Long.class, OrderDeliveryDTO.class, OrderExtra.class)) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
        return this.handleIt(this.cast(params[0], Long.class), this.cast(params[1], OrderDeliveryDTO.class)
                , this.cast(params[2], OrderExtra.class));
    }

    /**
     * 真实发货处理
     *
     * @param supplierId    供应商id
     * @param orderDelivery 发货信息
     * @return 包裹id
     */
    public abstract SupplierOrderPackage handleIt(Long supplierId, OrderDeliveryDTO orderDelivery, OrderExtra orderExtra);
}
