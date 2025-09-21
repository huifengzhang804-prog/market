package com.medusa.gruul.carrier.pigeon.service.model.dto;

import com.medusa.gruul.carrier.pigeon.api.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


/**
 * @author 张治保
 * date 2022/10/12
 */
@Getter
@Setter
public class ChatMessage {

    /**
     * 消息类型
     */
    @NotNull
    private MessageType messageType;

    /**
     * 消息内容
     */
    @NotBlank
    private String message;

}
