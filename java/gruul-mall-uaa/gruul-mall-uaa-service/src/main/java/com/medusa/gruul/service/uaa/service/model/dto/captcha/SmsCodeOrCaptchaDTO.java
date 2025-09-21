package com.medusa.gruul.service.uaa.service.model.dto.captcha;

import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import lombok.Data;

/**
 * 
 * @author jipeng
 * @since 2025/4/10
 */
@Data
public class SmsCodeOrCaptchaDTO {

    private CaptchaResponse<ImageCaptchaVO> captcha;
    private String smsCode;
}
