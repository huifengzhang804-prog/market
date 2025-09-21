package com.medusa.gruul.addon.supplier.model.dto;

import com.medusa.gruul.common.model.base.ProductSkuKey;
import com.medusa.gruul.global.model.o.KeyValue;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/7/27
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderStorageDTO implements Serializable {

    /**
     * 订单号
     */
    @NotBlank
    private String orderNo;

    /**
     * 店铺 id
     */
    @Null
    private Long shopId;

    /**
     * 备注
     */
    @Size(max = 500)
    private String remark;

    /**
     * 每个 sku 入库详情 key: skuKey value: 入库数量
     */
    @NotNull
    @Size(min = 1)
    @EachSkuStorageValid(message = "入库数需要大于 1")
    private Set<KeyValue<ProductSkuKey, Integer>> skuStorages;

    // 自定义注解定义
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = EachSkuStorageValidator.class)
    public @interface EachSkuStorageValid {
        String message() default "入库数需要大于0";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    // 校验器实现
    public static class EachSkuStorageValidator implements ConstraintValidator<EachSkuStorageValid, Set<KeyValue<ProductSkuKey, Integer>>> {
        @Override
        public boolean isValid(Set<KeyValue<ProductSkuKey, Integer>> set, ConstraintValidatorContext context) {
            if (set == null) {
                // @NotNull 已处理空值
                return true;
            }
            return set.stream()
                    .allMatch(kv -> kv != null && kv.getValue() != null && kv.getValue() >= 1);
        }
    }

}
