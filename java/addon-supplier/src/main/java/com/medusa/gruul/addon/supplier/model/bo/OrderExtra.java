package com.medusa.gruul.addon.supplier.model.bo;

import com.medusa.gruul.addon.supplier.model.dto.OrderPayDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/7/20
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderExtra implements Serializable {

    /**
     * 备注
     */
    private String remark;

    /**
     * 收货人地址信息
     */
    private Receiver receiver;


    /**
     * 支付信息
     */
    private OrderPayDTO pay;

    /**
     * 订单支付超时时间
     */
    private Long payTimeout;

}
