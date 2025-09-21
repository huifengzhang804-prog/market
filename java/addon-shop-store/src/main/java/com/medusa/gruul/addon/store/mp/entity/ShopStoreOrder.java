package com.medusa.gruul.addon.store.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.order.api.enums.PackageStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 店铺门店订单
 *
 * @author xiaoq
 * @Description 店铺门店订单
 * @date 2023-03-16 16:15
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
@TableName(value = "t_shop_store_order", autoResultMap = true)
public class ShopStoreOrder extends BaseEntity {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 买家id
     */
    private Long buyerId;

    /**
     * 订单包裹状态
     */
    private PackageStatus packageStatus;


    /**
     * 支付金额
     */
    private Long payAmount;


    /**
     * 店铺门店id
     */
    private Long shopStoreId;


    /**
     * 提货时间
     */
    private String pickUpTime;


    /**
     * 当前日期
     */
    private LocalDate presentDate;
    /**
     * 核销码
     */

    private String code;


    private String orderPackage;
}
