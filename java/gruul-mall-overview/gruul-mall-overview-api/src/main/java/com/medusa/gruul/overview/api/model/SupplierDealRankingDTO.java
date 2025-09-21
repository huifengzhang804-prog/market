package com.medusa.gruul.overview.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * <p>deal ranking dto封装</p>
 * @author An.Yan
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class SupplierDealRankingDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4052940587261067186L;

    /**
     * 订单号
     */
    private String no;

    /**
     * 供应商ID
     */
    private Long supplierId;


    /**
     * 创建订单的用户店铺ID
     */
    private Long shopId;


    /**
     * 需要支付的金额
     */
    private Long payAmount;

    /**
     * 主单号
     */
    private String mainNo;

    /**
     * 订单供应商备注
     */
    private String remark;

    /**
     * 订单项
     */
    private List<SupplierOrderItem> orderItems;

    @Data
    @NoArgsConstructor
    public static class SupplierOrderItem implements Serializable {

        @Serial
        private static final long serialVersionUID = 147558568070165340L;

        /**
         * 商品ID
         */
        private Long productId;

        /**
         * 购买数量
         */
        private Integer num;

        /**
         * 销售单价
         */
        private Long salePrice;

        /**
         * 成交单价
         */
        private Long dealPrice;

        /**
         * 修正价格
         */
        private Long fixPrice;

        /**
         * 运费
         */
        private Long freightPrice;
    }
}
