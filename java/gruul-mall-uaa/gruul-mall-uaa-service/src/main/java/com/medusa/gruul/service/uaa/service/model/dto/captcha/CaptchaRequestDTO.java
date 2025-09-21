package com.medusa.gruul.service.uaa.service.model.dto.captcha;

import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 滑块验证码 请求格式 兼容 jakarta
 *
 * @author 张治保
 * date 2022/10/28
 * @see cloud.tianai.captcha.spring.request.CaptchaRequest
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CaptchaRequestDTO<T> extends cloud.tianai.captcha.spring.request.CaptchaRequest<T> {

    /**
     * 验证码 id
     */
    @NotEmpty
    private String id;

    /**
     * 滑动轨迹
     */
    @NotNull
    private ImageCaptchaTrack captchaTrack;

    /**
     * 提交的表单数据
     */
    @Valid
    @NotNull
    private T form;
}