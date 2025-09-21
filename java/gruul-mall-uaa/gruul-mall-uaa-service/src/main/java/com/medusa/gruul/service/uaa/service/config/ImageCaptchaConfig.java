package com.medusa.gruul.service.uaa.service.config;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.common.constant.SliderCaptchaConstant;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.common.model.dto.ResourceMap;
import cloud.tianai.captcha.resource.impl.DefaultResourceStore;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import cloud.tianai.captcha.spring.exception.CaptchaValidException;
import cloud.tianai.captcha.spring.store.CacheStore;
import cloud.tianai.captcha.spring.store.impl.RedisCacheStore;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.service.uaa.service.model.enums.UaaError;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static cloud.tianai.captcha.generator.impl.StandardSliderImageCaptchaGenerator.DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH;

/**
 * 滑块验证码资源配置
 *
 * @author 张治保
 * date 2022/12/8
 */
@Configuration
public class ImageCaptchaConfig extends DefaultResourceStore implements InitializingBean {

    @Bean
    public CacheStore cacheStore(StringRedisTemplate stringRedisTemplate) {
        return new RedisCacheStore(stringRedisTemplate);
    }

    @Override
    public void afterPropertiesSet() {
        String tag = "default";
        // 滑块验证码 模板 (系统内置)
        ResourceMap template1 = new ResourceMap(tag, 4);
        template1.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/active.png")));
        template1.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/1/fixed.png")));
        ResourceMap template2 = new ResourceMap(tag, 4);
        template2.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/active.png")));
        template2.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/2/fixed.png")));
        // 旋转验证码 模板 (系统内置)
        ResourceMap template3 = new ResourceMap(tag, 4);
        template3.put(SliderCaptchaConstant.TEMPLATE_ACTIVE_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/active.png")));
        template3.put(SliderCaptchaConstant.TEMPLATE_FIXED_IMAGE_NAME, new Resource(ClassPathResourceProvider.NAME, DEFAULT_SLIDER_IMAGE_TEMPLATE_PATH.concat("/3/fixed.png")));

        // 1. 添加一些模板
        addTemplate(CaptchaTypeConstant.SLIDER, template1);
        addTemplate(CaptchaTypeConstant.SLIDER, template2);
        addTemplate(CaptchaTypeConstant.ROTATE, template3);

        // 2. 添加自定义背景图片  该处可直接替换滑动图片 590*360 jpg
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "static/slider/a.jpg", tag));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "static/slider/b.jpg", tag));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "static/slider/c.jpg", tag));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "static/slider/d.jpg", tag));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "static/slider/e.jpg", tag));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "static/slider/f.jpg", tag));
        addResource(CaptchaTypeConstant.SLIDER, new Resource("classpath", "static/slider/g.jpg", tag));

    }


    @RestControllerAdvice
    public static class CaptchaValidResult {
        @ExceptionHandler(CaptchaValidException.class)
        public Result<Void> captchaValidError() {
            return Result.failed(UaaError.INVALID_SLIDE_CAPTCHA);
        }
    }

}
