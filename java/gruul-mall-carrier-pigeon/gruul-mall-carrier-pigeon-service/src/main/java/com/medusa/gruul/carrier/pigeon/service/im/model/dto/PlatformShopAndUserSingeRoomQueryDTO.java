package com.medusa.gruul.carrier.pigeon.service.im.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.carrier.pigeon.service.im.entity.ChatMessage;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * <p>平台/店铺-用户聊天查询DTO</p>
 *
 * @author An.Yan
 */
@Getter
@Setter
@ToString
public class PlatformShopAndUserSingeRoomQueryDTO extends Page<ChatMessage> {

    @NotNull
    private Long userId;


    @NotNull
    private Long shopId;
}
