package com.medusa.gruul.service.uaa.api.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.medusa.gruul.global.model.constant.RegexPools;
import com.medusa.gruul.service.uaa.api.constant.UaaConstant;
import com.medusa.gruul.service.uaa.api.enums.Gender;
import com.medusa.gruul.service.uaa.api.excel.GenericConverter;
import com.medusa.gruul.user.api.enums.MemberType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Data
public class UserRegisterDTO {

    /**
     * 用户姓名
     */
    @ExcelProperty("姓名（最多32个字）")
    @Size(max = 32, message = "姓名长度不能超过32个字")
    private String userName;
    /**
     * 性别
     */
    @ExcelProperty(value = "性别（男/女）", converter = GenericConverter.class)
    private Gender gender;
    /**
     * 生日
     */
    @ExcelProperty(value = "生日（比如：1989/08/08 或 1989-08-08）")
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDate birthday;


	/**
	 * 手机号
	 */
	@ExcelProperty("手机号（必填）")
	@NotBlank
	@Pattern(regexp = RegexPools.MOBILE_TEL,message = "手机号格式不正确")
	private String mobile;

	/**
	 * 用户总积分值
	 */
	@ExcelProperty("积分（限整数）")
	@Min(value = 0, message = "积分不能为负数")
	private Long integralTotal;
	/**
	 * 余额
	 */
	@ExcelProperty("储值余额（单位：元，精确小数点后两位）")
	@DecimalMin(value = "0.00", message = "储值余额不能为负数")
	private BigDecimal balance;


    /**
     * 会员类型
     */
    @ExcelProperty(value = "会员类型(免费/付费)", converter = GenericConverter.class)
    private MemberType memberType;


	/**
	 * 会员等级
	 */
	@ExcelProperty("会员等级值（VIP+等级值。如：VIP1）")
	@Pattern(regexp = UaaConstant.MEMBER_RANK_REGEX, message = "会员等级值格式不正确")
	private String rankCode;



    @ExcelIgnore
    private Integer memberRankCode;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserRegisterDTO userRegister) {
            if (this.mobile.equals(userRegister.getMobile())) {
                throw new IllegalArgumentException("repeat mobile number");
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, gender, birthday, mobile, integralTotal, balance, memberType, rankCode, memberRankCode);
    }

}
