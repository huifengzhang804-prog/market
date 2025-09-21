package com.medusa.gruul.carrier.pigeon.service.im.controller;

import com.medusa.gruul.carrier.pigeon.service.im.service.IPlatformShopAndUserChatMessageService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 平台/店铺-用户聊天室控制器
 *
 * @author An.Yan
 */
@RestController
@RequestMapping("/pigeon/platform-chat-rooms")
@PreAuthorize("""
        @S.matcher()
            .any(@S.ROLES,@S.USER,@S.PLATFORM_ADMIN,@S.SHOP_ADMIN,@S.SHOP_CUSTOM_ADMIN)
            .or(@S.consumer().eq(@S.ROLES,@S.PLATFORM_CUSTOM_ADMIN).eq(@S.PERMS,'message:customer:service'))
            .match()
        """)
@RequiredArgsConstructor
public class PlatformShopAndUserChatRoomController {

    private final IPlatformShopAndUserChatMessageService IPlatformShopAndUserChatMessageService;

    /**
     * 平台/店铺-C端用户创建聊天室
     *
     * @param shopId 店铺ID
     * @param userId 用户ID
     */
    @Log("创建聊天室")
    @PostMapping("/{shopId}/{userId}")
    public Result<Void> createPlatformChatRoom(@Valid @PathVariable("shopId") Long shopId, @Valid @PathVariable("userId") Long userId) {
        IPlatformShopAndUserChatMessageService.createRoom(shopId, userId);
        return Result.ok();
    }
}
