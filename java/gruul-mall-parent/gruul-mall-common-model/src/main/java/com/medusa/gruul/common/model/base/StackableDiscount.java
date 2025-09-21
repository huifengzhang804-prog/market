package com.medusa.gruul.common.model.base;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.Duration;

/**
 * 叠加优惠 与超时时间配置
 *
 * @author 张治保
 * date 2023/3/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class StackableDiscount implements java.io.Serializable {


    /**
     * 支付超时时间 为空使用订单默认的超时时间
     */
    private Duration payTimeout;

    /**
     * 是否使用优惠券 默认为true
     */
    private boolean coupon;

    /**
     * 是否能使用会员价 默认为true
     */
    private boolean vip;

    /**
     * 是否能使用满减 默认为true
     */
    private boolean full;

}
