package com.medusa.gruul.user.api.model.dto.paid;

import com.medusa.gruul.user.api.enums.paid.EffectiveDurationType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 付费规则json DTO addon
 *
 * @author xiaoq
 * @Description PaidRuleJsonDTO.java
 * @date 2022-11-15 10:17
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PaidRuleJsonDTO implements Serializable {

    private Long id;

    /**
     * 有效期
     */
    private EffectiveDurationType effectiveDurationType;

    /**
     * 价格
     */
    private Long price;

}
