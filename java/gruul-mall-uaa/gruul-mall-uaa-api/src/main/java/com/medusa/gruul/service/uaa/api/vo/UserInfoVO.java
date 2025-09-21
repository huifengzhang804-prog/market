package com.medusa.gruul.service.uaa.api.vo;

import com.medusa.gruul.service.uaa.api.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/9/23
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserInfoVO implements Serializable {
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
	 * 用户邮箱
	 */
	private String email;
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
