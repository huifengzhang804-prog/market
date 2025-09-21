package com.medusa.gruul.addon.supplier.model.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 张治保
 * date 2023/7/24
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderItemExtra implements Serializable {

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 运费模板
     */
    private Long templateId;

}
