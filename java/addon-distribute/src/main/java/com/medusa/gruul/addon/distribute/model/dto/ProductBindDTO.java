package com.medusa.gruul.addon.distribute.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/11/14
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductBindDTO extends BonusConfigDTO implements Serializable {

    /**
     * 商品id集合
     */
    @NotNull
    @NotEmpty
    private Set<Long> productIds;


}
