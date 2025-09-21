package com.medusa.gruul.overview.api.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2022/11/30
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopBalanceVO implements Serializable {

    /**
     * 总余额 不包含待结算
     */
    private Long total;

    /**
     * 待提现余额
     */
    private Long undrawn;

    /**
     * 待结算余额
     */
    private Long uncompleted;


    public static ShopBalanceVO defaultBalance() {
        return new ShopBalanceVO()
                .setTotal(0L)
                .setUndrawn(0L)
                .setUncompleted(0L);
    }

}
