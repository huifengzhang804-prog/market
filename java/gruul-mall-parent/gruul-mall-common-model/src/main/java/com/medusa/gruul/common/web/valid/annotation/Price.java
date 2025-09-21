package com.medusa.gruul.common.web.valid.annotation;

import com.medusa.gruul.common.web.valid.validator.PriceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 最低限额
 *
 * @author xiaoq
 * @Description minimum.java
 * @date 2022-10-20 15:30
 */

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RUNTIME)
@Repeatable(Price.Prices.class)
@Constraint(validatedBy = {PriceValidator.class})
public @interface Price {

    String message() default "com.medusa.gruul.common.web.valid.annotation.Price.massage";


    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface Prices {
        Price[] value();
    }
}
