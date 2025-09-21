package com.medusa.gruul.order.service.model.dto;

import com.medusa.gruul.global.model.constant.RegexPools;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * @author 张治保
 * date 2022/10/22
 */
@Getter
@Setter
@ToString
public class OrderRemarkDTO {

    /**
     * 总订单号 或者 店铺订单号
     * 平台端 为总订单号列表
     * 商家端 为店铺订单号列表
     */
    @NotNull
    @Size(min = 1)
    private Set<String> nos;

    /**
     * 备注
     */
    @NotNull
    @Pattern(regexp = RegexPools.NOT_BLANK)
    private String remark;
}
