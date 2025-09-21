package com.medusa.gruul.order.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单票据dto
 *
 * @author xiaoq
 * @Description OrderBillDTO.java
 * @date 2023-08-16 17:08
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderBillDTO implements Serializable {
    /**
     * 订单完成时间
     */
    private LocalDateTime orderAccomplishTime;


    /**
     * 票据金额
     */
    private Long billMoney;
}
