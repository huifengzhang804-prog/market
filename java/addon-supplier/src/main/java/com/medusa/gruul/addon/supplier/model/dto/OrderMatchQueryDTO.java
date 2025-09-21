package com.medusa.gruul.addon.supplier.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/7/26
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderMatchQueryDTO implements Serializable {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 供应商 id
     */
    private Long supplierId;

    /**
     * 店铺 id
     */
    private Long shopId;


}
