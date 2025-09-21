package com.medusa.gruul.common.model.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/6/9
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class ShopOrderKey implements Serializable {
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 店铺id
     */
    private Long shopId;
}
