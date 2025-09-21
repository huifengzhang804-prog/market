package com.medusa.gruul.carrier.pigeon.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessagePageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.dto.MessageUserPageQueryDTO;
import com.medusa.gruul.carrier.pigeon.service.model.vo.MessageUserVO;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.Message;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageShopAdmin;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageService;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageShopAdminService;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageUserService;
import com.medusa.gruul.carrier.pigeon.service.service.MessageQueryService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2022/10/10
 */
@Service
@RequiredArgsConstructor
public class MessageQueryServiceImpl implements MessageQueryService {

    private final IMessageService messageService;
    private final IMessageUserService messageUserService;
    private final IMessageShopAdminService messageShopAdminService;

    @Override
    public IPage<MessageUserVO> messageUserPage(MessageUserPageQueryDTO query) {
        SecureUser user = ISecurity.userMust();
        query.setShopId(user.getShopId());
        IPage<MessageUserVO> disable = TenantShop.disable(() -> messageUserService.messageUserPage(user.getId(), query));
        List<MessageUserVO> records = disable.getRecords();
        if (CollUtil.isEmpty(records)){
            return disable;
        }
        records.forEach(messageUserVO -> {
            //商家未读用户消息
            messageUserVO.setUnreadNumber(messageService.getShopUnRead(user.getId(),query.getShopId(),messageUserVO.getLastMessage().getUserId()));
        });
        return disable;
    }

    @Override
    public IPage<Message> messagePage(MessagePageQueryDTO query) {
        Long shopId = query.getShopId();
        MessagePageQueryDTO messagePage = TenantShop.disable(
                () -> messageService.lambdaQuery()
                        .eq(Message::getUserId, query.getUserId())
                        .eq(Message::getShopId, shopId)
                        .orderByDesc(Message::getCreateTime)
                        .page(query)
        );
        if (messagePage.getTotal() < CommonPool.NUMBER_ONE) {
            return messagePage;
        }
        Set<Long> adminIds = messagePage.getRecords().stream().filter(message -> message.getAdminId() != null).collect(Collectors.toSet()).stream().map(Message::getAdminId).collect(Collectors.toSet());
        if (CollUtil.isEmpty(adminIds)) {
            return messagePage;
        }
        Map<Long, String> adminIdNicknameMap = TenantShop.disable(() -> messageShopAdminService.lambdaQuery()
                        .in(MessageShopAdmin::getUserId, adminIds)
                        .eq(MessageShopAdmin::getShopId, shopId)
                        .list())
                .stream()
                .collect(Collectors.toMap(MessageShopAdmin::getUserId, MessageShopAdmin::getNickname));
        if (CollUtil.isEmpty(adminIdNicknameMap)) {
            return messagePage;
        }
        messagePage.getRecords().stream().filter(message -> message.getAdminId() != null).forEach(
                message -> message.setNickname(adminIdNicknameMap.get(message.getAdminId()))
        );
        return messagePage;
    }
}
