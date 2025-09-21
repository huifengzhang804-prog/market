package com.medusa.gruul.addon.integral.model.dto;

import com.medusa.gruul.global.model.constant.RegexPools;
import com.medusa.gruul.global.model.helper.Address;
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
 * 积分订单收货信息dto
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class IntegralReceiverDTO implements Address {
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
