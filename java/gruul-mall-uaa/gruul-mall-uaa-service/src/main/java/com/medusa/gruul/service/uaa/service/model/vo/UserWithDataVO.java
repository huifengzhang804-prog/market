package com.medusa.gruul.service.uaa.service.model.vo;

import cn.hutool.core.util.DesensitizedUtil;
import com.medusa.gruul.common.fastjson2.annotation.Desensitize;
import com.medusa.gruul.service.uaa.api.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2022/5/24
 */
@Getter
@Setter
@ToString
public class UserWithDataVO {
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
	@Desensitize(start = 3, end = 3)
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
