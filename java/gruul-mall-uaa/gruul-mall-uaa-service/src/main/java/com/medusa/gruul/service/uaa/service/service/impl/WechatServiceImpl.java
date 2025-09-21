package com.medusa.gruul.service.uaa.service.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.scheme.WxMaGenerateSchemeRequest;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.service.uaa.api.constant.UaaConstant;
import com.medusa.gruul.service.uaa.service.model.dto.wechat.WeChatCodeDTO;
import com.medusa.gruul.service.uaa.service.model.dto.wechat.WechatSchemaDTO;
import com.medusa.gruul.service.uaa.service.model.enums.WechatType;
import com.medusa.gruul.service.uaa.service.service.WechatService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author 张治保
 * date 2022/11/21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatServiceImpl implements WechatService {

	private final RedissonClient redissonClient;
	private WxMaService wxMaService;
	private WxMpService wxMpService;


	@Override
	@SneakyThrows
	public WxMaJscode2SessionResult getSession(String code) {
		return wxMaService.getUserService()
				.getSessionInfo(code);
	}

	@Override
	@SneakyThrows
	public WxMaPhoneNumberInfo getPhoneInfo(String code) {
		return wxMaService.getUserService()
				.getPhoneNoInfo(code);
	}
	@Override
	@SneakyThrows
	public String getWeChatCode(WeChatCodeDTO weChatCode) {
		String appid = wxMaService.getWxMaConfig().getAppid();
		String cacheKey = RedisUtil.key(UaaConstant.WECHAT_CODE_PREFIX, appid, weChatCode.getPath());
		String code = RedisUtil.getCacheObject(cacheKey);
		if (StrUtil.isNotEmpty(code)) {
			return code;
		}
		RLock lock = redissonClient.getLock(RedisUtil.getLockKey(UaaConstant.WECHAT_CODE_LOCK, appid));
		if (!lock.tryLock(CommonPool.NUMBER_TEN, TimeUnit.SECONDS)) {
			throw SystemCode.SYSTEM_BUSY.exception();
		}
		byte[] wxaCodeBytes;
		try {
			wxaCodeBytes = wxMaService.getQrcodeService().createWxaCodeBytes(
					weChatCode.getPath(),
					weChatCode.getEnvVersion(),
					weChatCode.getWidth(),
					weChatCode.getAutoColor(),
					weChatCode.getLineColor(),
					weChatCode.getIsHyaline()
			);
		} finally {
			lock.unlock();
		}
		code = "data:image/png;base64," + Base64.encode(wxaCodeBytes);
		RedisUtil.setCacheObject(cacheKey, code);
		return code;
	}

	@Override
	public WxJsapiSignature jsapiSignature(WechatType type, String url) {
		try {
			return WechatType.MP == type ? wxMpService.createJsapiSignature(url) : wxMaService.getJsapiService().createJsapiSignature(url);
		} catch (WxErrorException ex) {
			log.error("获取jsapi签名失败", ex);
			throw SystemCode.FAILURE.exception();
		}
	}

	@Override
	public String getWeChatSchemaUrl(WechatSchemaDTO wechatSchema) {
		String path = wechatSchema.getPath();
		String query = UrlQuery.of(wechatSchema.getQuery()).toString();
		String key = RedisUtil.key(UaaConstant.WECHAT_SCHEMA_PREFIX, wechatSchema.getEnvVersion(), path + query);
		String schema = RedisUtil.getCacheObject(key);
		if (StrUtil.isNotEmpty(schema)) {
			return schema;
		}
		try {
			schema = wxMaService.getWxMaSchemeService().generate(
					WxMaGenerateSchemeRequest.newBuilder()
							.jumpWxa(
									WxMaGenerateSchemeRequest.JumpWxa.newBuilder()
											.path(path)
											.query(query)
											.envVersion(wechatSchema.getEnvVersion())
											.build()
							)
							.isExpire(true)
							.expireType(CommonPool.NUMBER_ONE)
							.expireInterval(wechatSchema.getExpireDays())
							.build()
			);
		} catch (WxErrorException ex) {
			log.error("获取jsapi签名失败", ex);
			throw SystemCode.FAILURE.exception();
		}
		RedisUtil.setCacheObject(key, schema, wechatSchema.getExpireDays(), TimeUnit.DAYS);
		return schema;
	}

	@Lazy
	@Autowired
	public void setWxMaService(WxMaService wxMaService) {
		this.wxMaService = wxMaService;
	}

	@Lazy
	@Autowired
	public void setWxMpService(WxMpService wxMpService) {
		this.wxMpService = wxMpService;
	}
}
