package com.medusa.gruul.service.uaa.service.model.dto.scancode;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 可以重定向到对应页面的二维码 生成参数
 *
 * @author 张治保
 * date 2023/5/10
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RedirectQrCodeDTO implements Serializable {

	/**
	 * 是否是hash模式
	 */
	private Boolean hash = Boolean.TRUE;

	/**
	 * 基础网站访问路径
	 */
	private String baseUrl;

	/**
	 * 跳转路径
	 */
	private String path;

	/**
	 * 跳转参数
	 */
	private JSONObject params;

	/**
	 * 是否需要跳转
	 */
	private Boolean codeRedirect = Boolean.TRUE;

}
