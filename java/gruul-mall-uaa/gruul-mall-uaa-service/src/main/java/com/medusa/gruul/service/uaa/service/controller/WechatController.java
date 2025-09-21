package com.medusa.gruul.service.uaa.service.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.service.uaa.service.model.dto.wechat.WeChatCodeDTO;
import com.medusa.gruul.service.uaa.service.model.dto.wechat.WechatSchemaDTO;
import com.medusa.gruul.service.uaa.service.model.enums.WechatType;
import com.medusa.gruul.service.uaa.service.service.WechatService;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import org.springframework.web.bind.annotation.*;

/**
 * @author 张治保
 * date 2023/5/10
 */
@RestController
@RequestMapping("/uaa/wx")
@RequiredArgsConstructor
public class WechatController {

	private final WechatService wechatService;

	/**
	 * 获取小程序码
	 *
	 * @return 小程序码
	 */
	@Log("获取小程序码")
	@GetMapping("/chat")
	public Result<String> getWeChatCode(WeChatCodeDTO weChatCode) {
		return Result.ok(wechatService.getWeChatCode(weChatCode));
	}

	/**
	 * 获取小程序 schema url
	 *
	 * @return 小程序 schema url
	 */
	@Log("获取小程序 schema url")
	@GetMapping("/schema")
	public Result<String> getWeChatSchemaUrl(WechatSchemaDTO wechatSchema) {
		return Result.ok(wechatService.getWeChatSchemaUrl(wechatSchema));
	}


	/**
	 * 创建调用jsapi时所需要的签名
	 *
	 * @return 签名
	 */
	@GetMapping("/jsapi/signature/{type}")
	public Result<WxJsapiSignature> jsapiSignature(@PathVariable WechatType type, @RequestParam String url) {
		return Result.ok(wechatService.jsapiSignature(type, url));
	}


}
