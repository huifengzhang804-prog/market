package com.medusa.gruul.common.model.message;

import com.medusa.gruul.common.model.enums.BalanceHistoryOperatorType;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.message.annotation.Avail;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * @author xiaoq
 * @Description 数据变化消息
 * @date 2022-10-08 15:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataChangeMessage implements BaseDTO {


    @Serial
    private static final long serialVersionUID = -5131834418843317810L;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 变化值
     */
    @NotNull
    @Avail
    private Long value;
    /**
     * 变化后的值
     */
    private Long afterValue;

    /**
     * 变化类型
     */
    @NotNull
    private ChangeType changeType;

    /**
     * 扩展字段
     */
    private String extendInfo;

    /**
     * 操作人id
     */
    private Long operatorUserId;

    /**
     * 操作类型
     */
    private BalanceHistoryOperatorType operatorType;

    /**
     * 关联订单
     */
    private String orderNo;
    /**
     * 主题
     */
    private String subject;

    /**
     * 交易流水号
     */
    private String transactionSerialNumber;

    @Override
    public void validParam() {
        if (getUserId() == null) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
        }
    }
}
