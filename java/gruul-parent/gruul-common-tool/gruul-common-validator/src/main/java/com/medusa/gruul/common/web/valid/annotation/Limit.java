package com.medusa.gruul.common.web.valid.annotation;

import com.medusa.gruul.common.web.valid.validator.LimitValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotNull;
import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * valid 支持枚举/范围类型的注解
 *
 *
 *
 * @author 张治保
 * date 2021/3/23
 */

@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RUNTIME)
@Documented
@Repeatable(Limit.Limits.class)
@Constraint(validatedBy = {LimitValidator.class})
@NotNull
public @interface Limit {

    String message() default "com.medusa.gruul.common.web.valid.Limit.message";


    String[] value() default {};


    String min() default "0";

    String max() default "10";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default { };
    
    @Target({ElementType.FIELD,ElementType.PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface Limits{
        Limit[] value();
    }
}
