package com.medusa.gruul.addon.ic.modules.ic.model.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2024/8/27
 */

@Getter
@Setter
@Accessors(chain = true)
public class ICOrderTimes implements Serializable {

    /**
     * 发货时间
     */
    private LocalDateTime shippingTime;

    /**
     * 接单时间
     */
    private LocalDateTime takeOrderTime;

    /**
     * 到店时间
     */
    private LocalDateTime arrivalShopTime;

    /**
     * 取货时间
     */
    private LocalDateTime pickupTime;

    /**
     * 送达时间
     */
    private LocalDateTime deliveredTime;

    /**
     * 发生错误的时间
     */
    private LocalDateTime errorTime;


    /**
     * 异常处理时间
     */
    private LocalDateTime errorHandleTime;

}
