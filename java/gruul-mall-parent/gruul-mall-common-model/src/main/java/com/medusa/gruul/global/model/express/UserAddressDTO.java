package com.medusa.gruul.global.model.express;

import com.medusa.gruul.global.model.constant.RegexPools;
import com.medusa.gruul.global.model.helper.Address;
import com.vividsolutions.jts.geom.Point;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author 张治保
 * date 2022/8/9
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserAddressDTO implements Address {
    /**
     * 姓名
     */
    @NotBlank
    private String name;

    /**
     * 联系方式
     */
    @NotBlank
    @Pattern(regexp = RegexPools.MOBILE_TEL)
    private String mobile;

    /**
     * 用户地址定位
     */
    private Point location;

    /**
     * 省市区
     */
    @NotNull
    @Size(min = 2, max = 3)
    private List<String> area;

    /**
     * 详细地址
     */
    @NotBlank
    private String address;
}
