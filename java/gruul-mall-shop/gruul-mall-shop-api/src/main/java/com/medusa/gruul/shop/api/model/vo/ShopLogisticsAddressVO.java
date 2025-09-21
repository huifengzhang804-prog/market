package com.medusa.gruul.shop.api.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * date 2022/8/10
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class ShopLogisticsAddressVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 联系人名称
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 邮编码
     */
    private String zipCode;

    /**
     * 省市区名称
     */
    private List<String> area;

    /**
     * 详细地址
     */
    private String address;
}
