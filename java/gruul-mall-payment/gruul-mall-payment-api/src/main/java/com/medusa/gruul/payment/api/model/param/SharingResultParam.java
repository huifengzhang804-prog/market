package com.medusa.gruul.payment.api.model.param;

import com.medusa.gruul.global.model.enums.ReceiverType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * date 2023/6/21
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class SharingResultParam implements Serializable {

    /**
     * 分账单号
     */
    @NotBlank
    private String sharingNo;

    /**
     * 分账接收方类型
     */
    @NotNull
    private ReceiverType receiverType;

    /**
     * 分账接收方账号
     */
    private Long accountId;
}
