package com.medusa.gruul.user.api.model;

import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 张治保
 * date 2022/5/31
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserAddressVO {

    /**
     * 地址id
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 定位
     */
    private Point location;

    /**
     * 省市区号列表
     */
    private List<String> area;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 是否是默认地址
     */
    private Boolean isDefault;
}
