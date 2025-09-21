package com.medusa.gruul.carrier.pigeon.service.service.impl;

import com.medusa.gruul.carrier.pigeon.service.mp.entity.Message;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageShop;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageShopAdmin;
import com.medusa.gruul.carrier.pigeon.service.mp.entity.MessageUser;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageService;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageShopAdminService;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageShopService;
import com.medusa.gruul.carrier.pigeon.service.mp.service.IMessageUserService;
import com.medusa.gruul.carrier.pigeon.service.service.ShopAndUserService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import io.vavr.control.Option;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2022/10/12
 */
@Service
@RequiredArgsConstructor
public class ShopAndUserServiceImpl implements ShopAndUserService {


    private final UaaRpcService uaaRpcService;
    private final ShopRpcService shopRpcService;
    private final IMessageService messageService;
    private final IMessageShopService messageShopService;
    private final IMessageShopAdminService messageShopAdminService;
    private final IMessageUserService messageUserService;

    @Override
    public MessageShop checkAndGetMessageShop(Long shopId) {
        return Option.of(
                ISystem.shopId(shopId, () -> messageShopService.lambdaQuery().one())
        ).getOrElse(
                () -> Option.of(shopRpcService.getShopInfoByShopId(shopId))
                        .map(
                                shop -> {
                                    if (ShopStatus.NORMAL != shop.getStatus()) {
                                        throw new GlobalException("店铺不可用");
                                    }
                                    MessageShop messageShop = new MessageShop().setShopLogo(shop.getLogo()).setShopName(shop.getName());
                                    ISystem.shopId(shopId, () -> messageShopService.save(messageShop));
                                    return messageShop;
                                }
                        )
                        .getOrElseThrow(() -> new GlobalException("店铺信息不存在"))
        );
    }

    @Override
    public MessageShopAdmin checkAndGetMessageShopAdmin(Long shopId, Long adminId) {
        return Option.of(
                ISystem.shopId(shopId, () -> messageShopAdminService.lambdaQuery().eq(MessageShopAdmin::getUserId, adminId).one())
        ).getOrElse(
                () -> {
                    MessageShopAdmin admin = new MessageShopAdmin().setUserId(adminId).setNickname("管理员");
                    uaaRpcService.getShopUserDataByShopIdUserId(shopId, adminId)
                            .peek(shopUser -> admin.setNickname(StringUtil.isEmpty(shopUser.getNickname()) ? " " : shopUser.getNickname()));
                    ISystem.shopId(shopId, () -> messageShopAdminService.save(admin));
                    return admin;
                }
        );
    }

    @Override
    public MessageUser checkAndGetMessageUser(Long userId) {
        return Option.of(
                messageUserService.lambdaQuery().eq(MessageUser::getUserId, userId).one()
        ).getOrElse(
                () -> uaaRpcService.getUserDataByUserId(userId)
                        .map(
                                user -> {
                                    MessageUser messageUser = new MessageUser()
                                            .setUserId(userId)
                                            .setNickname(StringUtil.isEmpty(user.getNickname()) ? " " : user.getNickname())
                                            .setAvatar(user.getAvatar());
                                    messageUserService.save(messageUser);
                                    return messageUser;
                                }
                        )
                        .getOrElseThrow(() -> new GlobalException("用户不存在"))
        );
    }

    @Override
    public Option<Long> getLastMessageAdminId(Long shopId, Long userId) {
        Long adminId = Option.of(
                        ISystem.shopId(
                                shopId,
                                () -> messageService.lambdaQuery()
                                        .eq(Message::getUserId, userId)
                                        .ge(Message::getCreateTime, LocalDateTime.now().minusDays(CommonPool.NUMBER_ONE))
                                        .orderByDesc(Message::getCreateTime)
                                        .last(CommonPool.SQL_LIMIT_1)
                                        .one()
                        )
                ).map(Message::getAdminId)
                .getOrNull();
        return Option.of(adminId);
    }
}
