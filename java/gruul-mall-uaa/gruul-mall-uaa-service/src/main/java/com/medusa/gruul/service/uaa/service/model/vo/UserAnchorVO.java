package com.medusa.gruul.service.uaa.service.model.vo;

import com.medusa.gruul.service.uaa.api.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author miskw
 * @date 2023/6/26
 * @describe 主播的用户
 */
@Getter
@Setter
@ToString
public class UserAnchorVO {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * openid
     */
    private String openid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户手机号
     */
    private String mobile;
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
}
