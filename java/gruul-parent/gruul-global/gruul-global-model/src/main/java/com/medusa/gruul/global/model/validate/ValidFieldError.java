package com.medusa.gruul.global.model.validate;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author 张治保
 * @since 2023/11/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ValidFieldError implements Serializable {

    /**
     * 字段访问路径
     */
    private String filed;

    /**
     * 错误信息
     */
    private String message;


    public static <T> List<ValidFieldError> of(Set<ConstraintViolation<T>> violations) {
        if (violations == null || violations.isEmpty()) {
            return List.of();
        }
        return violations.stream()
                .map(
                        violation -> new ValidFieldError()
                                .setFiled(violation.getPropertyPath().toString())
                                .setMessage(violation.getMessage())
                ).toList();
    }
}
