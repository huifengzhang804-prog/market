package com.medusa.gruul.carrier.pigeon.service.im.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.im.entity.ChatMessage;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.PlatformShopAndUserMessageDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.PlatformShopAndUserPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.PlatformShopAndUserSingeRoomQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.vo.PlatformShopAndUserMultiChatRoomVO;
import com.medusa.gruul.carrier.pigeon.service.im.service.IPlatformShopAndUserChatMessageService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 平台/店铺-用户消息Controller
 */
@RestController
@RequestMapping("/pigeon/platform-chat-room-messages")
@PreAuthorize("""
        @S.matcher()
            .any(@S.ROLES,@S.USER,@S.PLATFORM_ADMIN, @S.SHOP_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'message:customer:service'))
            .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'message:customer:service'))
            .match()
        """)
@RequiredArgsConstructor
public class PlatformShopAndUserChatRoomMessageController {

    private final IPlatformShopAndUserChatMessageService chatMessageService;


    /**
     * (平台/店铺)  --用户消息列表
     *
     * @param dto {@link PlatformShopAndUserMessageDTO}
     */
    @Log("(平台/店铺)- C端用户之间发送消息")
    @PostMapping("/message")
    public Result<Void> sendMessage(@Valid @RequestBody PlatformShopAndUserMessageDTO dto) {
        chatMessageService.sendMessage(dto);
        return Result.ok();
    }


    /**
     * 分页查询(平台/店铺) --用户消息列表
     *
     * @param dto {@link PlatformShopAndUserPageQueryDTO}
     * @return {@link PlatformShopAndUserMultiChatRoomVO}
     */
    @Log("分页查询(平台/店铺) --用户消息列表")
    @GetMapping("/chat-rooms")
    public Result<IPage<PlatformShopAndUserMultiChatRoomVO>>
    listMultiChatRoom(@Valid PlatformShopAndUserPageQueryDTO dto) {
        if (dto.getShopId() == null && ISystem.clientTypeMust() == ClientType.SHOP_CONSOLE) {
            dto.setShopId(ISystem.shopIdMust());
        }
        return Result.ok(chatMessageService.listMultiChatRoom(dto));
    }

    /**
     * 查询单聊天室消息(平台/店铺) --用户
     *
     * @param dto 查询对象,参见 {@link PlatformShopAndUserSingeRoomQueryDTO}
     * @return {@link ChatMessage}
     */
    @Log("查询单聊天室消息(平台/店铺) --用户")
    @GetMapping("/chat-room")
    public Result<IPage<ChatMessage>> listSingleChatRoomMessages(PlatformShopAndUserSingeRoomQueryDTO dto) {
        if (ISystem.clientTypeMust() == ClientType.SHOP_CONSOLE) {
            dto.setShopId(ISystem.shopIdMust());
        }
        return Result.ok(chatMessageService.listSingleChatRoomMessages(dto));
    }

}
