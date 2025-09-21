package com.medusa.gruul.user.service.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class UserExportVO {

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号", index = 0)
    private String userPhone;

    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名", index = 1)
    private String userName;

    /**
     * 用户性别
     */
    @ExcelProperty(value = "性别", index = 2)
    private String gender;

    /**
     * 生日
     */
    @ExcelProperty(value = "生日", index = 3)
    private LocalDate birthday;

    /**
     * 储值余额
     */
    @ExcelProperty(value = "储值余额", index = 4)
    private BigDecimal balance;

    /**
     * 消费总金额
     */
    @ExcelProperty(value = "消费总金额", index = 5)
    private BigDecimal dealTotalMoney;

    /**
     * 会员类型
     */
    @ExcelProperty(value = "会员类型", index = 6)
    private String memberType;
    /**
     * 会员等级值
     */
    @ExcelProperty(value = "会员等级值", index = 7)
    private Integer rankCode;

    /**
     * 成长值
     */
    @ExcelProperty(value = "成长值", index = 8)
    private Long growthValue;

    /**
     * 积分
     */
    @ExcelProperty(value = "积分", index = 9)
    private Long integralTotal;
}
