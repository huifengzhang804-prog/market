package com.medusa.gruul.common.web.valid;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Description: 关闭快速返回
 *
 * @author alan
 * Date: 2019/9/4 21:01
 */
@Configuration
public class ValidatorConfig {

    @Bean
    public Validator validator() {
        try (
            ValidatorFactory validatorFactory = Validation
                    .byProvider(HibernateValidator.class)
                    .configure()
                    //快速返回模式，有一个验证失败立即返回错误信息
                    .failFast(false)
                    .buildValidatorFactory()
        ){
            return validatorFactory.getValidator();
        }
    }

    /**
     * JSR和Hibernate validator的校验只能对Object的属性进行校验
     * 不能对单个的参数进行校验
     * spring 在此基础上进行了扩展
     * 添加了MethodValidationPostProcessor拦截器
     * 可以实现对方法参数的校验
     */
    @Bean
    public MethodValidationPostProcessor getMethodValidationPostProcessor() {
        MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validator());
        return processor;
    }
}
