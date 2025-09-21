package com.medusa.gruul.common.web.valid.validator;

import com.medusa.gruul.common.web.valid.annotation.Price;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;


/**
 * minimum 验证
 *
 * @author xiaoq
 * @Description MinimumValidator.java
 * @date 2022-10-20 15:36
 */
@Slf4j
public class PriceValidator implements ConstraintValidator<Price, Object> {

    private Price price;


    @Override
    public void initialize(Price price) {
        this.price = price;
    }

    /**
     * Implements the validation logic.
     * The state of {@code value} must not be altered.
     * <p>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (!(value instanceof Long money)) {
            return false;
        }
        return money.equals(0L) || money % 100 == 0;
    }
}
