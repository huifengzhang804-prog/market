package com.medusa.gruul.payment.api.model.vo;

import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.payment.api.enums.DealType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 储值订单历史vo
 *
 * @author xiaoq
 * @Description SavingsOrderHistoryVO.java
 * @date 2022-12-23 09:40
 */
@Data
public class SavingsOrderHistoryVO {

    /**
     * 余额历史表id
     */
    private Long id;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 金额
     */
    private Long money;


    /**
     * 交易类型
     */
    private DealType dealType;

    /**
     * 变化类型
     */
    private ChangeType changeType;


    /**
     * 支付渠道
     */
    private PayType payType;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 会员昵称
     */
    private String userNikeName;

    /**
     * 会员头像
     */
    private String userHeadPortrait;

}
