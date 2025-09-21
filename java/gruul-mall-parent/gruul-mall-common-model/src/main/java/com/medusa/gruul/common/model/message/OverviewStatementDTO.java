package com.medusa.gruul.common.model.message;

import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.enums.TransactionType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 对账单DTO
 * @author: WuDi
 * @date: 2022/10/14
 */
@Data
@Accessors(chain = true)
public class OverviewStatementDTO {


    /**
     * 交易流水号
     */
    private String transactionSerialNumber;

    /**
     * 用户id
     */
    private Long userId;


    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 交易类型
     */
    private TransactionType transactionType;

    /**
     * 收支金额
     */
    private Long account;

    /**
     * 订单id
     */
    private String orderNo;

    /**
     * 订单商品项id
     */
    private Long shopOrderItemId;

    /**
     * 包裹id
     */
    private Long packageId;

    /**
     * 售后工单号
     */
    private String afsNo;
    /**
     * 资金流向:0->收入;1->支出
     */
    private ChangeType changeType;

    /**
     * 交易时间
     */
    private LocalDateTime transactionTime;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 扩展字段
     */
    private String extendInfo;
}
