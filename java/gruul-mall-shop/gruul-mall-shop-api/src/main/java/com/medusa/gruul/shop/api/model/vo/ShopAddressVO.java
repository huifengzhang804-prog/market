package com.medusa.gruul.shop.api.model.vo;

import com.medusa.gruul.global.model.helper.Address;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 张治保
 * @since 2024/8/23
 */
@Getter
@Setter
@Accessors(chain = true)
public class ShopAddressVO implements Address {

    /**
     * 定位
     */
    private Point location;

    /**
     * 省市区
     */
    private List<String> area;

    /**
     * 详细地址
     */
    private String address;
}
