package com.medusa.gruul.common.model.message.annotation;


import com.medusa.gruul.common.model.message.validator.AvailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 有效金额
 *
 * @author xiaoq
 * @Description Avail.java
 * @date 2023-10-12
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RUNTIME)
@Repeatable(Avail.Avails.class)
@Constraint(validatedBy = {AvailValidator.class})
public @interface Avail {

    String message() default "操作金额有误";


    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface Avails {
        Avail[] value();
    }
}
