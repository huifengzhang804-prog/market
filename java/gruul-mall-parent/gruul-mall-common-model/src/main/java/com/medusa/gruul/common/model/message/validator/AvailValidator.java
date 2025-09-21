package com.medusa.gruul.common.model.message.validator;

import com.medusa.gruul.common.model.message.annotation.Avail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


/**
 * 有效金额校验
 *
 * @author xiaoq
 * @Description AvailValidator.java
 * @date 2023-10-12 13:34
 */
public class AvailValidator implements ConstraintValidator<Avail, Object> {

    private Avail avail;


    @Override
    public void initialize(Avail avail) {
        this.avail = avail;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            // 如果值为null 不通过校验
            return false;
        }
        if (!(value instanceof Long)) {
            // 如果值不是Long类型，不通过校验
            return false;
        }

        long longValue = (long) value;

        // 校验是否属于有效金额 最小值0.01
        return longValue % 100 == 0;
    }

}
