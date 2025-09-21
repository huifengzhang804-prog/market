package com.medusa.gruul.carrier.pigeon.service.modules.message;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.ChatMessage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessagePageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessageShopPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageShopVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.Message;
import com.medusa.gruul.carrier.pigeon.service.service.MessageQueryService;
import com.medusa.gruul.carrier.pigeon.service.service.UserMessageService;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 用户消息
 *
 * @author 张治保
 * date 2022/10/10
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/pigeon/message")
@PreAuthorize("@S.matcher().role(@S.R.USER).match()")
public class UserMessageController {

    private final UserMessageService userMessageService;
    private final MessageQueryService messageQueryService;

    /**
     * 移动端
     * 用户分页查询 消息页  店铺列表
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    @Log("用户分页查询 消息页  店铺列表")
    @GetMapping("/shop")
    public Result<IPage<MessageShopVO>> messageShopPage(MessageShopPageQueryDTO query) {
        return Result.ok(
                userMessageService.messageShopPage(query)
        );
    }

    /**
     * 分页查询聊天记录
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    @Log("分页查询聊天记录")
    @GetMapping("/shop/{shopId}/message")
    public Result<IPage<Message>> messagePage(MessagePageQueryDTO query) {
        query.setUserId(ISecurity.userMust().getId());
        return Result.ok(
                messageQueryService.messagePage(query)
        );
    }

    /**
     * 未读消息的管理端消息设置为已读
     *
     * @param shopId 管理端id  shopId 0 平台  other 店铺
     */
    @Log("用户未读消息设置为已读")
    @PutMapping("/shop/{shopId}/read")
    public Result<Void> readMessages(@PathVariable Long shopId) {
        userMessageService.readMessages(ISecurity.userMust().getId(), shopId);
        return Result.ok();
    }

    /**
     * 发送消息给店铺
     *
     * @param shopId      店铺id
     * @param chatMessage 消息内容
     */
    @Log("发送消息给店铺")
    @PostMapping("/shop/{shopId}/message")
    public Result<Void> sendMessage(@PathVariable Long shopId, @RequestBody @Valid ChatMessage chatMessage) {
        userMessageService.sendMessage(ISecurity.userMust().getId(), shopId, chatMessage);
        return Result.ok();
    }

    /**
     * 统计
     *
     * @return 未读消息条数
     */
    @Log("统计所有未读消息")
    @GetMapping("/my/unread/count")
    public Result<Long> getMyUnreadMessageCount() {
        return Result.ok(
                userMessageService.getUnreadMessageCountByUserId(ISecurity.userMust().getId())
        );
    }

    /**
     * 用户发送消息给平台
     *
     * @param chatMessage 消息内容
     * @return Void
     */
    @PostMapping("/send/")
    @Log("发送消息给平台")
    public Result<Void> sendMessageToPlatform(@RequestBody @Valid ChatMessage chatMessage) {
        userMessageService.sendMessageToPlatform(ISecurity.userMust().getId(), chatMessage);
        return Result.ok();
    }


}
