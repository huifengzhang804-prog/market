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
public class GoodParam {
    /**
     * name	string	是
     * 名称
     */
    private String name;

    /**
     * count	string	是
     * 数量
     */
    private Long count;

    /**
     * price	string	是
     * 单价 单位元
     */
    private BigDecimal price;

    /**
     * amount	string	是
     * 总价
     */
    private BigDecimal amount;
}
