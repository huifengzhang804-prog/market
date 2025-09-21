package com.medusa.gruul.order.service.model.dto;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.global.model.constant.RegexPools;
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
 * 收货人地址详情
 *
 * @author 张治保
 * date 2022/6/8
 */
@Getter
@Setter
@ToString
public class ReceiverDTO {
    /**
     * 收货人姓名
     */
    @NotBlank
    private String name;

    /**
     * 收货人电话/手机号
     */
    @NotBlank
    @Pattern(regexp = RegexPools.MOBILE_TEL)
    private String mobile;

    /**
     * 收货人经纬度定位
     */
    @NotNull
    private Point location;

    /**
     * 收货人省市区
     */
    @NotNull
//    @Size(min = 2, max = 3)
    private List<String> area;

    /**
     * 详细地址
     */
    @NotBlank
    private String address;


    public boolean empty() {
        return CollUtil.isEmpty(area) || StrUtil.isEmpty(name) || StrUtil.isEmpty(mobile) || StrUtil.isEmpty(address);
    }

}
