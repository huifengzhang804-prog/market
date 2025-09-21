package com.medusa.gruul.service.uaa.service.model.vo;

import cn.hutool.core.util.DesensitizedUtil;
import com.medusa.gruul.common.fastjson2.annotation.Desensitize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2022/10/26
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserAccountVO {

	/**
	 * 用户名
	 */
	@Desensitize(start = 3, end = 3)
	private String username;
	/**
	 * 用户手机号
	 */
	@Desensitize(type = DesensitizedUtil.DesensitizedType.MOBILE_PHONE)
	private String mobile;
	/**
	 * 用户邮箱
	 */
	@Desensitize(type = DesensitizedUtil.DesensitizedType.EMAIL)
	private String email;

	/**
	 * 用户openid
	 */
	private String openid;

	/**
	 * 管理手机号 未脱敏
	 */
	private String manageMobile;

}
