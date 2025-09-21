package com.medusa.gruul.user.api.model.dto;

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
public class UserDataDTO implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String username;


    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 用户性别
     */
    private String gender;

    /**
     * 生日
     */
    private LocalDate birthday;


}
