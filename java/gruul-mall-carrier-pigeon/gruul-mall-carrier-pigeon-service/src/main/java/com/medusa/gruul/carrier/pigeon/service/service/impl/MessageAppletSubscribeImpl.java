package com.medusa.gruul.carrier.pigeon.service.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.medusa.gruul.carrier.pigeon.api.enums.SubscribeMsg;
import com.medusa.gruul.carrier.pigeon.api.model.dto.SubscribeAppletMsgDTO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageAppletSubscribe;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageAppletSubscribeService;
import com.medusa.gruul.carrier.pigeon.service.service.MessageAppletSubscribeService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.TenantShop;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author WuDi
 * date: 2023/1/10
 */
@Service
@DS("carrier-pigeon")
@RequiredArgsConstructor
public class MessageAppletSubscribeImpl implements MessageAppletSubscribeService {

	private static final String SEND_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token={}";
	private static final String GET_ACCESSTOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid={}&secret={}";
	private final IMessageAppletSubscribeService messageAppletSubscribeService;
	private final WxMaService wxMaService;

	/**
	 * 发送小程序订阅消息
	 *
	 * @param subscribeAppletMsg 小程序订阅消息
	 */
	@Override
	public void sendAppletSubscribe(SubscribeAppletMsgDTO subscribeAppletMsg) {
		MessageAppletSubscribe messageAppletSubscribe = TenantShop.disable(() ->
				messageAppletSubscribeService.lambdaQuery()
						.eq(MessageAppletSubscribe::getSubscribeMsg, subscribeAppletMsg.getSubscribeMsg().getValue())
						.one());
		if (ObjectUtils.isEmpty(messageAppletSubscribe)) {
			return;
		}
		WxMaConfig wxMaConfig = wxMaService.getWxMaConfig();
		Map<String, Object> map = new HashMap<>(CommonPool.NUMBER_SIX);
		map.put("touser", subscribeAppletMsg.getOpenid());
		map.put("template_id", messageAppletSubscribe.getTemplateId());
		map.put("page", messageAppletSubscribe.getPage());
		map.put("miniprogram_state", "formal");
		map.put("lang", "zh_CN");
		map.put("data", subscribeAppletMsg.getData());
		String access_token = HttpUtil.get(StrUtil.format(GET_ACCESSTOKEN, wxMaConfig.getAppid(), wxMaConfig.getSecret()));
		access_token = JSON.parseObject(access_token).getString("access_token");
		String msg = HttpUtil.post(StrUtil.format(SEND_MESSAGE, access_token), JSON.toJSONString(map));
		System.out.println("wxMaConfig msg = " + msg);

	}

	/**
	 * 获取小程序模板id
	 *
	 * @return 模板id
	 */
	@Override
	public Map<SubscribeMsg, String> getTemplateIds() {
		List<MessageAppletSubscribe> appletSubscribes = TenantShop.disable(
				() -> messageAppletSubscribeService.lambdaQuery()
						.select(MessageAppletSubscribe::getSubscribeMsg, MessageAppletSubscribe::getTemplateId)
						.list()
		);
		if (CollectionUtils.isEmpty(appletSubscribes)) {
			return Collections.emptyMap();
		}
		return appletSubscribes.stream()
				.collect(Collectors.toMap(MessageAppletSubscribe::getSubscribeMsg, MessageAppletSubscribe::getTemplateId));
	}
}
