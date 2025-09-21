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
