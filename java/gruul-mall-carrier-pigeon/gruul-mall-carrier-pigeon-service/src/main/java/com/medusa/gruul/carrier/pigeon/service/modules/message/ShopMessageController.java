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
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 商家客服消息
 *
 * @author 张治保
 * date 2022/10/10
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/pigeon/message")
@PreAuthorize("@S.shopPerm('message:customer:service')")
public class ShopMessageController {

    private final ManageMessageService shopMessageService;
    private final MessageQueryService messageQueryService;

    /**
     * 店铺分页查询聊天是 用户列表
     * 商家端
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    @Log("分页查询聊天室用户列表")
    @GetMapping("/user")
    public Result<IPage<MessageUserVO>> messageUserPage(MessageUserPageQueryDTO query) {
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
    @GetMapping("/user/{userId}/message")
    public Result<IPage<Message>> messagePage(MessagePageQueryDTO query) {
        query.setShopId(ISecurity.userMust().getShopId());
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
        shopMessageService.sendMessage(userId, chatMessage);
        return Result.ok();
    }


}
