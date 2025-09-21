package com.medusa.gruul.carrier.pigeon.service.im.model.dto;

import com.medusa.gruul.carrier.pigeon.api.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * <p>平台/店铺-用户消息DTO</p>
 *
 * @author An.Yan
 */
@Getter
@Setter
@ToString
public class PlatformShopAndUserMessageDTO {

    /**
     * 发送方ID
     */
    @NotNull
    private Long senderId;

    /**
     * 接收方ID
     */
    @NotNull
    private Long receiverId;


    /**
     * 消息类型
     */
    @NotNull
    private MessageType messageType;

    /**
     * 消息内容
     */
    @NotBlank
    private String content;
}
