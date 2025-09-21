package com.medusa.gruul.carrier.pigeon.service.im.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.service.im.entity.ChatMessage;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * <p>单个用户聊天记录查询Dto</p>
 *
 * @author An.Yan
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class SingleChatListQueryDTO extends Page<ChatMessage> {
    /**
     * 接收方店铺ID
     */
    @NotNull
    private Long receiverShopId;

    /**
     * 接收方用户ID
     */
    @NotNull
    private Long receiverUserId;

    /**
     * 发送方店铺ID
     */
    @NotNull
    private Long senderShopId;

    /**
     * 发送方用户ID
     */
    @NotNull
    private Long senderUserId;
}
