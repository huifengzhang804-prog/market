package com.medusa.gruul.order.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 订单目的地信息
 *
 * @author 张治保
 * @since 2024/9/5
 */
@Getter
@Setter
@Accessors(chain = true)
public class OrderDestination implements Serializable {

    /**
     * 目的地名称
     */
    private String name;

    /**
     * 目的地电话
     */
    private String mobile;

    /**
     * 目的地地址
     */
    private String address;
}
