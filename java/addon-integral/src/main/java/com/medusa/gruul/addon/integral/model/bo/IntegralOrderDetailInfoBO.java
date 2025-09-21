package com.medusa.gruul.addon.integral.model.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 积分订单信息bo
 *
 * @author shishuqian
 * date 2023/2/2
 * time 13:32
 **/
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class IntegralOrderDetailInfoBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -6959489324548651040L;
    /**
     * 用户id
     */
    private Long buyerId;
    /**
     * 积分订单id
     */
    private String integralOrderNo;
}
