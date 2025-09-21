package com.medusa.gruul.overview.api.model;

import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.enums.TransactionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2022/11/24
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class StatementCreateDTO implements Serializable {

    /**
     * 交易类型
     */
    private TransactionType tradeType;

    /**
     * 交易流水号
     */
    private String tradeNo;

    /**
     * 交易时间
     */
    private LocalDateTime tradeTime;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 店铺id 平台为 0
     */
    private Long shopId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 交易额
     */
    private Long amount;

    /**
     * 类型
     */
    private ChangeType changeType;
}
