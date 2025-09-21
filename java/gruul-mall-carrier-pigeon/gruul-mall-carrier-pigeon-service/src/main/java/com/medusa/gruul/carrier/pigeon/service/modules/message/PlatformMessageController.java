package com.medusa.gruul.carrier.pigeon.service.modules.message;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.ChatMessage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessagePageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessageUserPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageUserVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.Message;
import com.medusa.gruul.carrier.pigeon.service.service.ManageMessageService;
import com.medusa.gruul.carrier.pigeon.service.service.MessageQueryService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * @author xiaoq
 * @Description
 * @date 2023-06-10 13:59
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/pigeon/message/platform")
@PreAuthorize("@S.superAdmin")
public class PlatformMessageController {

    private final ManageMessageService platformMessageService;

    private final MessageQueryService messageQueryService;

    /**
     * 平台分页查询聊天室 用户列表
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    @Log("分页查询聊天室用户列表")
    @GetMapping("/user")
    public Result<IPage<MessageUserVO>> messageUserPlatformPage(MessageUserPageQueryDTO query) {
        query.setShopId(ISecurity.userMust().getShopId());
        return Result.ok(
                messageQueryService.messageUserPage(query)
        );
    }


    /**
     * 分页查询聊天记录
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    @Log("分页查询聊天记录")
    @GetMapping("user/{userId}/message")
    public Result<IPage<Message>> messagePage(MessagePageQueryDTO query) {
        query.setShopId((long)CommonPool.NUMBER_ZERO);
        return Result.ok(
                messageQueryService.messagePage(query)
        );
    }


    /**
     * 发送消息给用户
     */
    @Log("发送消息给用户")
    @PostMapping("/user/{userId}/message")
    public Result<Void> sendMessage(@PathVariable Long userId, @RequestBody @Valid ChatMessage chatMessage) {
        platformMessageService.sendMessageToUser(userId, chatMessage);
        return Result.ok();
    }
}
