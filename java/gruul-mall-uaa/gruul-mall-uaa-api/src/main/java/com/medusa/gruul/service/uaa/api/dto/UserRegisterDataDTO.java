package com.medusa.gruul.service.uaa.api.dto;

import com.medusa.gruul.service.uaa.api.enums.Gender;
import com.medusa.gruul.user.api.enums.MemberType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用户批量注册（导入）数据传输对象 发送给User 服务的 mq消息
 *
 * @author 张治保
 * @since 2024/6/12
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserRegisterDataDTO implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 性别
     */
    private Gender gender;
    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 用户总积分值
     */
    private Long integralTotal;
    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 会员类型
     */
    private MemberType memberType;

    /**
     * 会员等级
     */
    private Integer rankCode;


}
