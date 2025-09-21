package com.medusa.gruul.addon.supplier.model.bo;

import com.medusa.gruul.global.model.constant.RegexPools;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * date 2023/7/20
 */
@Getter
@Setter
@ToString
public class Receiver implements Serializable {
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
