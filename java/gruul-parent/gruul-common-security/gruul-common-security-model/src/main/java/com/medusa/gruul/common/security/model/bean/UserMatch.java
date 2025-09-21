package com.medusa.gruul.common.security.model.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * user match
 *
 * @author 张治保
 * date 2023/4/11
 */
@Getter
@Setter
@Builder
public class UserMatch implements java.io.Serializable {

	/**
	 * 是否权限匹配成功
	 */
	private boolean success;

	/**
	 * 当前用户信息
	 */
	private SecureUser secureUser;
}
