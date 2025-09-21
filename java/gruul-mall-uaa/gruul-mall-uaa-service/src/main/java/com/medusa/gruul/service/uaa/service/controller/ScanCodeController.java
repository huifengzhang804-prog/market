package com.medusa.gruul.service.uaa.service.controller;

import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.service.uaa.service.model.dto.scancode.RedirectQrCodeDTO;
import com.medusa.gruul.service.uaa.service.service.ScanCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 扫描码生成策略
 *
 * @author 张治保
 * date 2023/5/10
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/uaa/scan")
public class ScanCodeController {

	private final ScanCodeService scanCodeService;


	/**
	 * 生成普通二维码 小程序扫码可以跳小程序 h5扫码跳h5
	 */
	@Log("生成可重定向的二维码")
	@PostMapping("/qrcode/redirect")
	public Result<String> redirectQrcode(@RequestBody RedirectQrCodeDTO redirect) {
		return Result.ok(scanCodeService.redirectQrcode(redirect));
	}

}
