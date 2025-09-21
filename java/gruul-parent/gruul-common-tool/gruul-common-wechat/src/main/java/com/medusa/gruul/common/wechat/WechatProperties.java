package com.medusa.gruul.common.wechat;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 张治保
 * date 2023/1/9
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "gruul.wechat")
public class WechatProperties {

	/**
	 * 小程序id | app id
	 */
	private String appId;

	/**
	 * 小程序密钥 | app secret
	 */
	private String appSecret;


	/**
	 * 公众号id | app id
	 */
	private String mpAppId;

	/**
	 * 公众号密钥 | app secret
	 */
	private String mpAppSecret;

	/**
	 * 小程序发货上传是否开启
	 */
	private Boolean miniAppDeliver;
}
