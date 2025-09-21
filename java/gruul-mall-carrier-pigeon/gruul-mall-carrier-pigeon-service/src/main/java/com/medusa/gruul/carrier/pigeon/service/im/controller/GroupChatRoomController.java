package com.medusa.gruul.carrier.pigeon.service.im.controller;

import com.medusa.gruul.carrier.pigeon.service.im.service.IGroupChatRoomMessageService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 群聊聊天室Controller
 *
 * @author An.Yan
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/pigeon/group-chat-rooms")
@PreAuthorize("""
            @S.matcher()
                .any(@S.ROLES,@S.SHOP_ADMIN,@S.R.SUPPLIER_ADMIN)
                .or(@S.consumer().eq(@S.ROLES,@S.SHOP_CUSTOM_ADMIN).eq(@S.PERMS,'message:customer:service'))
                .or(@S.consumer().eq(@S.ROLES,@S.R.SUPPLIER_CUSTOM_ADMIN).eq(@S.PERMS,'message:customer:service'))
                .match()
        """)
public class GroupChatRoomController {

    private final IGroupChatRoomMessageService groupChatRoomMessageService;

    /**
     * 根据供应商ID-店铺ID创建聊天室
     *
     * @param fromShopId 消息发送方的店铺ID
     * @param toShopId   消息接收方的店铺ID
     */
    @Log("创建聊天室")
    @PostMapping("/{fromShopId}/{toShopId}")
    public Result<Void> createGroupChatRoom(@PathVariable("fromShopId") Long fromShopId, @PathVariable("toShopId") Long toShopId) {
        groupChatRoomMessageService.createGroupChatRoom(fromShopId, toShopId);
        return Result.ok();
    }
}
