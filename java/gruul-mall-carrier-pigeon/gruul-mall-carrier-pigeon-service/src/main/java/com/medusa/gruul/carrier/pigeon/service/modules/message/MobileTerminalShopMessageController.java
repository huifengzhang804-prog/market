package com.medusa.gruul.carrier.pigeon.service.modules.message;

import com.medusa.gruul.carrier.pigeon.service.service.ManageMessageService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 移动端商家后台
 *
 * @author miskw
 * @date 2023/3/1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/pigeon/message/mobileTerminal")
public class MobileTerminalShopMessageController {
    private final ManageMessageService shopMessageService;

    /**
     * 商家端已读用户发送的消息
     */
    @PutMapping("/user/readUser")
    public Result<Void> readUserMessages() {
        SecureUser secureUser = ISecurity.userMust();
        shopMessageService.readUserMessages(secureUser.getShopId(), secureUser.getId());
        return Result.ok();
    }

    /**
     * 根据用户id已读聊天记录
     *
     * @param userId 用户id
     */
    @PutMapping("/user/readUser/{userId}")
    public Result<Void> readUserMessagesByUserId(@PathVariable Long userId) {
        SecureUser secureUser = ISecurity.userMust();
        shopMessageService.readUserMessagesByUserId(userId, secureUser.getShopId(), secureUser.getId());
        return Result.ok();
    }

    /**
     * 移动端删除消息列表消息
     *
     * @param userId 用户Id
     */
    @Idem
    @PutMapping("/user/{userId}/deleteMessage")
    public Result<Void> deleteMessage(@PathVariable Long userId) {
        SecureUser secureUser = ISecurity.userMust();
        shopMessageService.deleteMessage(secureUser.getShopId(), secureUser.getId(), userId);
        return Result.ok();
    }

    /**
     * 移动端商家后台未读消息总数
     */
    @GetMapping("/user/shop/unread/count")
    public Result<Long> getUserToShopUnreadCount() {
        SecureUser secureUser = ISecurity.userMust();
        return Result.ok(shopMessageService.getUserToShopUnreadCount(secureUser.getShopId(), secureUser.getId()));
    }
}
