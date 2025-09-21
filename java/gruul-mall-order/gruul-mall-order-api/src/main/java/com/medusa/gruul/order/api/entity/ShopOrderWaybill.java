package com.medusa.gruul.order.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 *
 * 订单物流运单信息表
 *
 *
 * @author 张治保
 * @since 2022-06-08
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_shop_order_waybill")
public class ShopOrderWaybill extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 包裹id
     */
    private Long packageId;

    /**
     * 店铺支付的运费
     */
    private BigDecimal freightPrice;

    /**
     * 物流公司
     */
    private String company;

    /**
     * 物流公司代码
     */
    private String companyCode;

    /**
     * 运单号
     */
    private String no;

    /**
     * 集包地名称
     */
    private String packageName;

    /**
     * 集包地代码
     */
    private String packageCode;

    /**
     * 分拣码
     */
    private String sortingCode;


}
