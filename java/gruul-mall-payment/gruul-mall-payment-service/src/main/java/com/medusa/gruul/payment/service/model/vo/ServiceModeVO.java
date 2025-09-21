package com.medusa.gruul.payment.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/6/9
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ServiceModeVO implements Serializable {

	/**
	 * 是否开启了服务商模式
	 */
	private Boolean serviceEnable;

	/**
	 * 用户 微信openid
	 */
	private String openid;
}
