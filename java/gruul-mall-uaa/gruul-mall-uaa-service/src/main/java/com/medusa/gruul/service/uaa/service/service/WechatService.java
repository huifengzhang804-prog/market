package com.medusa.gruul.service.uaa.service.service;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.medusa.gruul.service.uaa.service.model.dto.wechat.WeChatCodeDTO;
import com.medusa.gruul.service.uaa.service.model.dto.wechat.WechatSchemaDTO;
import com.medusa.gruul.service.uaa.service.model.enums.WechatType;
import me.chanjar.weixin.common.bean.WxJsapiSignature;

/**
 * @author 张治保
 * date 2022/11/21
 */
public interface WechatService {


	/**
	 * 根据login js code 换取 session key
	 *
	 * @param code js code
	 * @return session key 与 open id
	 */
	WxMaJscode2SessionResult getSession(String code);

	/**
	 * 根据获取手机号专用code 换取手机号信息
	 *
	 * @param code 获取手机号专用code
	 * @return 手机号信息
	 */
	WxMaPhoneNumberInfo getPhoneInfo(String code);

	/**
	 * 获取小程序码
	 *
	 * @param weChatCode 生成小程序码dto
	 * @return base64
	 */
	String getWeChatCode(WeChatCodeDTO weChatCode);

	/**
	 * 创建调用jsapi时所需要的签名
	 *
	 * @param type 微信app类型
	 * @param url  当前网页的URL，不包含#及其后面部分
	 * @return 签名
	 */
	WxJsapiSignature jsapiSignature(WechatType type, String url);

	/**
	 * 获取小程序 schema url
	 *
	 * @param wechatSchema schema 渲染参数
	 * @return 小程序 schema url
	 */
	String getWeChatSchemaUrl(WechatSchemaDTO wechatSchema);
}
