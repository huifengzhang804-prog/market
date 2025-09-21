package com.medusa.gruul.order.service.model.bo;

import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.order.service.model.enums.OrderQueryStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;


/**
 * @author 张治保
 * date 2022/6/23
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopOrderQueryBO {

    /**
     * 订单号列表
     */
    private Set<String> orderNos;

    /**
     * 买家id
     */
    private Long buyerId;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 店铺订单号
     */
    private String shopOrderNo;

    /**
     * 查询状态
     */
    private OrderQueryStatus status;
    /**
     * 指定店铺id的订单
     */
    private Set<Long> shopIds;

    /**
     * 商品销售方式list
     */
    private Set<SellType> sellTypes;

    /**
     * 指定供应商id的订单
     */
    private Long supplierId;
}
