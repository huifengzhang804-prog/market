package com.medusa.gruul.addon.supplier.model.bo;

import com.medusa.gruul.addon.supplier.model.enums.PurchaseOrderPaymentMethodEnum;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @author jipeng
 * @since 2025/3/1
 */
@Data
public class PaymentMethod {
    /**
     * 支付方式
     */
    @Size.List(@Size(min = 1, max = 2))
    private List<PurchaseOrderPaymentMethodEnum> paymentMethods;

    public static PaymentMethod initInstance(){
        PaymentMethod method=new PaymentMethod();
        method.setPaymentMethods(List.of(PurchaseOrderPaymentMethodEnum.SHOP_BALANCE,
                PurchaseOrderPaymentMethodEnum.PAY_OFF_LINE));
        return method;
    }
}
