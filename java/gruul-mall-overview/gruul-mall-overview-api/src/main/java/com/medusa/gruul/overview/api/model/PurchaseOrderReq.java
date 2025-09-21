package com.medusa.gruul.overview.api.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 采购订单入参
 */
@Getter
@Setter
public class PurchaseOrderReq implements Serializable {

    @Serial
    private static final long serialVersionUID = -1490973357332333752L;
    /**
     * 采购单订单号
     */
    @NotNull
    private String orderNo;

    /**
     * 商家店铺ID
     */
    @NotNull
    private Long businessShopId;

    /**
     * 供应商店铺ID
     */
    @NotNull
    private Long supplierShopId;

    /**
     * 采购单商品集合
     */
    @NotNull
    private List<PurchaseOrderGoodsItem> items;

    @NotNull
    private Long freight;

    @Getter
    @Setter
    public static class PurchaseOrderGoodsItem implements Serializable {

        @Serial
        private static final long serialVersionUID = -1917390779357537935L;

        /**
         * 采购单商品ID
         */
        private Long productId;

        private Integer num;


        /**
         * 采购单商品金额
         */
        @NotNull
        private Long amount;

        /**
         * 采购单商品费率
         */
        @NotNull
        private Long rate;
    }


}
