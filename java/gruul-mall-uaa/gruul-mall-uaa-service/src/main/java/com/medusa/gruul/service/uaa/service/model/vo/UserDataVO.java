package com.medusa.gruul.service.uaa.service.model.vo;

import com.medusa.gruul.service.uaa.api.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author 张治保
 * @since 2024/6/12
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserDataVO implements Serializable {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别
     */
    private Gender gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 分销码
     */
    private String distributorCode;
    /**
     * 手机号码
     */
    private String phone;
}
