package com.medusa.gruul.service.uaa.service.service;

import com.medusa.gruul.service.uaa.service.model.dto.scancode.RedirectQrCodeDTO;

/**
 * @author 张治保
 * date 2023/5/10
 */
public interface ScanCodeService {


	/**
	 * 生成普通二维码 小程序扫码可以跳小程序 h5扫码跳h5
	 *
	 * @param redirect 生成二维码dto
	 * @return base64 二维码
	 */
	String redirectQrcode(RedirectQrCodeDTO redirect);
}
