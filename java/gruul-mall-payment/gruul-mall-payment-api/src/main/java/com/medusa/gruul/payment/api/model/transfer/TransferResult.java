package com.medusa.gruul.payment.api.model.transfer;

import com.medusa.gruul.payment.api.enums.TransferEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2022/11/28
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class TransferResult implements Serializable {

    /**
     * 转账付款交易单号 转账付款流水号
     */
    private String tradNo;

    /**
     * 转账付款时间 交易时间
     */
    private LocalDateTime tradeTime;
    /**
     * 转账状态
     */
    private TransferEnum status;


}
