package com.medusa.gruul.addon.rebate.mp.entity;

import com.medusa.gruul.user.api.enums.MemberType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RebateUsersExtendValue {


    /**
     * id
     */
    private Long id;

    /**
     * 会员类型
     */
    @NotNull
    private MemberType memberType;

    /**
     * 会员名称
     */
    @NotBlank
    private String memberName;

    /**
     * 会员级别
     */
    @NotNull
    @Min(1)
    private Integer rankCode;

    /**
     * 返利百分比
     */
    @NotNull
    @Min(0)
    private Long rebatePercentage;


    /**
     * 返利支付百分比
     */
    @NotNull
    @Min(0)
    @Max(value = 9000, message = "返利支付百分比 不可大于90%")
    private Long rebatePaymentPercentage;


    /**
     * 提现门槛
     */
    @NotNull
    @Min(0)
    private Long withdrawalThreshold;

}
