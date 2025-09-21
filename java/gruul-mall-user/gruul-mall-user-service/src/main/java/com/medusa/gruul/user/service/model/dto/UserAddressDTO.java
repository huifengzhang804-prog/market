package com.medusa.gruul.user.service.model.dto;

import cn.hutool.core.lang.RegexPool;
import com.vividsolutions.jts.geom.Point;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 收货地址DTO
 *
 * @author 张治保
 * date 2022/5/31
 */

@Getter
@Setter
@ToString
public class UserAddressDTO {

    /**
     * 姓名
     */
    @NotBlank
    private String name;
    /**
     * 联系电话
     */
    @NotBlank
    @Pattern(regexp = RegexPool.MOBILE)
    private String mobile;

    /**
     * 物理定位
     */
    @NotNull
    private Point location;

    /**
     * 省市区
     */
    @NotNull
    @Size(min = 2, max = 3, message = "请选择省市区")
    private List<String> area;

    /**
     * 详细地址
     */
    @NotBlank
    @Size(max = 150)
    private String address;

    /**
     * 是否设置默认收获地址
     */
    private Boolean isDefault;
}
