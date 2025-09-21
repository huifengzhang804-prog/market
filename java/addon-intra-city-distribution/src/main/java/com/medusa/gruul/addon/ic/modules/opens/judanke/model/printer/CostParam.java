package com.medusa.gruul.addon.ic.modules.opens.judanke.model.printer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author 张治保
 * @since 2024/8/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CostParam {
    /**
     * name	string	是
     * 名称
     */
    private String name;

    /**
     * amount	string	是
     * 价格 单位元
     */
    private BigDecimal amount;

}
