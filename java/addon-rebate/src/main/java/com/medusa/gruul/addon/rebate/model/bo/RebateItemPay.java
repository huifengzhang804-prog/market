package com.medusa.gruul.addon.rebate.model.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/9/13
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RebateItemPay implements Serializable {

    /**
     * 商品项数量
     */
    private Integer num;

    /**
     * 使用的返利金额
     */
    private Long paidRebate;
}
