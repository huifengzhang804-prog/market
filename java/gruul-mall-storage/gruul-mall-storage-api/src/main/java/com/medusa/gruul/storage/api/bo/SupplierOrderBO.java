package com.medusa.gruul.storage.api.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: wufuzhong
 * @Date: 2024/02/02 17:19:26
 * @Description：供应商 订单 BO
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class SupplierOrderBO implements Serializable {

    private static final long serialVersionUID = -4690653424903186233L;

    /**
     * 订单号
     */
    private String no;

    /**
     * 主订单号
     */
    private String mainNo;
}
