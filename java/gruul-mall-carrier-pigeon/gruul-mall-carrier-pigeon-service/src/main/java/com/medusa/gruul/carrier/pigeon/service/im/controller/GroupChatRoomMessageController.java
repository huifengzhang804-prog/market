package com.medusa.gruul.carrier.pigeon.service.im.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.im.entity.ChatMessage;
import com.medusa.gruul.carrier.pigeon.service.im.entity.ChatMsg;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.MessageSupplierShopPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.dto.SingleChatListQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.im.model.vo.ChatRoomVO;
import com.medusa.gruul.carrier.pigeon.service.im.service.IGroupChatRoomMessageService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 群聊聊天室消息Controller
 *
 * @author An.Yan
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/pigeon/group-chat-room-messages")
@PreAuthorize("""
            @S.matcher()
                .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN,@S.R.SUPPLIER_CUSTOM_ADMIN)
                .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'message:customer:service'))
                .or(@S.consumer().eq(@S.ROLES,@S.R.SUPPLIER_CUSTOM_ADMIN).eq(@S.PERMS,'message:customer:service'))
                .match()
        """)
public class GroupChatRoomMessageController {

    private final IGroupChatRoomMessageService messageUpgradeService;

    /**
     * 供应商-店铺之间发送消息
     *
     * @param chatMsg 消息DTO,参见{@link ChatMsg}
     * @return {@link Void}
     */
    @Log("供应商-店铺之间发送消息")
    @PostMapping("/message")
    public Result<Void> sendMessage(@RequestBody @Valid ChatMsg chatMsg) {
        messageUpgradeService.sendMessage(chatMsg);
        return Result.ok();
    }

    /**
     * 分页查询供应商-店铺消息列表
     *
     * @param query 查询对象,参见 {@link MessageSupplierShopPageQueryDTO}
     * @return 分页查询结果, {@link ChatRoomVO}
     */
    @Log("分页查询供应商-店铺消息列表")
    @GetMapping("/chat-rooms")
    public Result<IPage<ChatRoomVO>> listMultiChatRoom(MessageSupplierShopPageQueryDTO query) {
        return Result.ok(messageUpgradeService.listMultiChatRoom(query));
    }

    /**
     * 查询单聊天室消息(供应商-店铺)
     *
     * @param query 查询对象,参见 {@link SingleChatListQueryDTO}
     * @return {@link ChatMessage}
     */
    @Log("查询单聊天室消息(供应商-店铺)")
    @GetMapping("/chat-room")
    public Result<IPage<ChatMessage>> listSingleChatRoomMessages(SingleChatListQueryDTO query) {
        return Result.ok(messageUpgradeService.listSingleChatRoomMessages(query));
    }
}
