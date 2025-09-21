package com.medusa.gruul.global.model.validate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;

/**
 * 手动 validate工具
 *
 * @author 张治保
 * @since 2023-10-25
 */
public class ValidateHelper {

    /**
     * 校验器
     */
    private static final Validator VALIDATOR;

    static {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            VALIDATOR = factory.getValidator();
        }
    }

    /**
     * 通过分组来校验实体类
     */
    public static <T> Set<ConstraintViolation<T>> validate(T t, Class<?>... groups) {
        if (t == null) {
            return Set.of();
        }
        return VALIDATOR.validate(t, groups);
    }

    /**
     * 通过分组来校验实体类
     *
     * @param t      实体类
     * @param groups 校验分组
     * @param <T>    实体类类型
     * @return 字段错误信息列表
     */
    public static <T> List<ValidFieldError> filedErrors(T t, Class<?>... groups) {
        return ValidFieldError.of(validate(t, groups));
    }
}