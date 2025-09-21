# com.medusa.gruul.common.wechat.WechatAutoConfigure

**文件路径**: `common\wechat\WechatAutoConfigure.java`

## 代码文档
///
@author 张治保
date 2023/1/9
 
///

///
小程序配置
	 
///

///
公众号配置
	 
///


## 文件结构
```
项目根目录
└── common\wechat
    └── WechatAutoConfigure.java
```

## 完整代码
```java
package com.medusa.gruul.common.wechat;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaRedissonConfigImpl;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpRedissonConfigImpl;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author 张治保
 * date 2023/1/9
 */
@EnableConfigurationProperties(WechatProperties.class)
@RequiredArgsConstructor
public class WechatAutoConfigure {


	/**
	 * 小程序配置
	 */
	@Bean
	@ConditionalOnMissingBean
	public WxMaService wxMaService(WechatProperties wechatProperties, RedissonClient redissonClient) {
		String appId = wechatProperties.getAppId();
		if (StringUtil.isBlank(appId)) {
			return null;
		}
		WxMaRedissonConfigImpl config = new WxMaRedissonConfigImpl(redissonClient, WechatConstant.MA_STORAGE_PREFIX);
		config.setAppid(appId);
		config.setSecret(wechatProperties.getAppSecret());

		WxMaService service = new WxMaServiceImpl();
		service.setWxMaConfig(config);
		return service;
	}


	/**
	 * 公众号配置
	 */
	@Bean
	@ConditionalOnMissingBean
	public WxMpService wxMpService(WechatProperties wechatProperties, RedissonClient redissonClient) {
		String mpAppId = wechatProperties.getMpAppId();
		if (StringUtil.isBlank(mpAppId)) {
			return null;
		}
		WxMpRedissonConfigImpl config = new WxMpRedissonConfigImpl(redissonClient, WechatConstant.MP_STORAGE_PREFIX);
		config.setAppId(mpAppId);
		config.setSecret(wechatProperties.getMpAppSecret());

		WxMpService mpService = new WxMpServiceImpl();
		mpService.setWxMpConfigStorage(config);
		return mpService;
	}

}

```

# com.medusa.gruul.common.wechat.WechatConstant

**文件路径**: `common\wechat\WechatConstant.java`

## 代码文档
///
@author 张治保
date 2023/1/9
 
///

///
微信 小程序 缓存前缀
	 
///

///
微信公众号 缓存前缀
	 
///


## 文件结构
```
项目根目录
└── common\wechat
    └── WechatConstant.java
```

## 完整代码
```java
package com.medusa.gruul.common.wechat;

/**
 * @author 张治保
 * date 2023/1/9
 */
public interface WechatConstant {

	/**
	 * 微信 小程序 缓存前缀
	 */
	String MA_STORAGE_PREFIX = "gruul:mall:wechat:ma";

	/**
	 * 微信公众号 缓存前缀
	 */
	String MP_STORAGE_PREFIX = "gruul:mall:wechat:mp";


}

```

# com.medusa.gruul.common.wechat.WechatProperties

**文件路径**: `common\wechat\WechatProperties.java`

## 代码文档
///
@author 张治保
date 2023/1/9
 
///

///
小程序id | app id
	 
///

///
小程序密钥 | app secret
	 
///

///
公众号id | app id
	 
///

///
公众号密钥 | app secret
	 
///

///
小程序发货上传是否开启
	 
///


## 文件结构
```
项目根目录
└── common\wechat
    └── WechatProperties.java
```

## 完整代码
```java
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

```

