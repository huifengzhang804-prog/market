package com.medusa.gruul.addon.rebate.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户消费返利表
 * </p>
 *
 * @author WuDi
 * @since 2023-07-17
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_rebate_transactions")
public class RebateTransactions extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;


    /**
     * 返利余额
     */
    private Long rebateBalance;

    /**
     * 累计返利
     */
    private Long accumulatedRebate;

    /**
     * 待结算返利
     */
    private Long unsettledRebate;

    /**
     * 已失效返利
     */
    private Long expiredRebate;



    public RebateTransactions init() {
        if (this.rebateBalance == null) {
            this.rebateBalance = 0L;
        }
        if (this.accumulatedRebate == null) {
            this.accumulatedRebate = 0L;
        }
        if (this.unsettledRebate == null) {
            this.unsettledRebate = 0L;
        }
        if (this.expiredRebate == null) {
            this.expiredRebate = 0L;
        }
        return this;
    }
}
