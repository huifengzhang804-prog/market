package com.medusa.gruul.user.service.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.o.Final;
import com.medusa.gruul.order.api.model.OrderPaidBroadcastDTO;
import com.medusa.gruul.user.api.constant.UserConstant;
import com.medusa.gruul.user.service.mp.entity.ShopUserAccount;
import com.medusa.gruul.user.service.mp.entity.UserAccount;
import com.medusa.gruul.user.service.mp.entity.UserDealDetail;
import com.medusa.gruul.user.service.mp.service.IShopUserAccountService;
import com.medusa.gruul.user.service.mp.service.IUserAccountService;
import com.medusa.gruul.user.service.mp.service.IUserDealDetailService;
import com.medusa.gruul.user.service.service.UserOrderDetailService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户明细
 *
 * @author xiaoq
 * @Description UserDetailsServiceImpl.java
 * @date 2022-11-28 16:28
 */
@Service
@RequiredArgsConstructor
public class UserOrderDetailServiceImpl implements UserOrderDetailService {

    private final IUserDealDetailService userDealDetailsService;
    private final IUserAccountService userAccountService;
    private final IShopUserAccountService shopUserAccountService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrderDetail(OrderPaidBroadcastDTO orderPaidBroadcast) {
        Long buyerId = orderPaidBroadcast.getBuyerId();
        UserAccount userAccount = userAccountService.lambdaQuery()
                .eq(UserAccount::getUserId, buyerId)
                .one();
        if (userAccount == null) {
            return;
        }
        // 更新用户最后交易时间
        userAccount.setLastDealTime(LocalDateTime.now());
        userAccountService.updateById(userAccount);
        // 获取店铺用户交易明细
        List<UserDealDetail> shopUserDealDetail = getShopUserDealDetail(orderPaidBroadcast, buyerId);
        shopUserDealDetail.add(
                new UserDealDetail()
                .setUserId(orderPaidBroadcast.getBuyerId())
                .setShopId(0L)
                .setPayType(orderPaidBroadcast.getOrderPayment().getType())
                .setGoodsAmount(orderPaidBroadcast.getPayAmount() + orderPaidBroadcast.getOrderPayment().getDiscountAmount())
                .setDiscountAmount(orderPaidBroadcast.getOrderPayment().getDiscountAmount())
                .setFreightAmount(orderPaidBroadcast.getOrderPayment().getFreightAmount())
                .setOrderNo(orderPaidBroadcast.getOrderNo())
                .setPayAmount(orderPaidBroadcast.getPayAmount())
        );
        boolean success = userDealDetailsService.saveBatch(shopUserDealDetail);
        if (!success) {
            throw new RuntimeException("保存用户订单明细失败");
        }
    }

    private List<UserDealDetail> getShopUserDealDetail(OrderPaidBroadcastDTO orderPaidBroadcast, Long buyerId) {
        return orderPaidBroadcast.getShopPayAmounts()
                .stream()
                .map(shopPayAmount -> {
                    Long shopId = shopPayAmount.getShopId();
                    boolean exists = shopUserAccountService.lambdaQuery()
                            .eq(ShopUserAccount::getShopId, shopId)
                            .eq(ShopUserAccount::getUserId, buyerId)
                            .exists();
                    if (exists) {
                        shopUserAccountService.lambdaUpdate()
                                .setSql(StrUtil.format(UserConstant.USER_SHOP_CONSUMPTION_SQL_TEMPLATE, shopPayAmount.getAmount()))
                                .eq(ShopUserAccount::getShopId, shopId)
                                .eq(ShopUserAccount::getUserId, buyerId)
                                .update();
                    }
                    return new UserDealDetail()
                            .setOrderNo(orderPaidBroadcast.getOrderNo())
                            .setUserId(buyerId)
                            .setShopId(shopId)
                            .setPayType(orderPaidBroadcast.getOrderPayment().getType())
                            .setDealTime(orderPaidBroadcast.getNotifyTime())
                            .setGoodsAmount(shopPayAmount.getAmount() + shopPayAmount.getDiscountAmount())
//                            .setGoodsAmount(shopPayAmount.getAmount() + shopPayAmount.getDiscountAmount() - shopPayAmount.getFreightAmount())
                            .setDiscountAmount(shopPayAmount.getDiscountAmount())
                            .setPayAmount(shopPayAmount.getAmount())
                            .setFreightAmount(shopPayAmount.getFreightAmount());
//                            .setPayAmount(shopPayAmount.getAmount() - shopPayAmount.getFreightAmount())
//                            .setFreightAmount(0L);
                }).collect(Collectors.toList());
    }

    @Override
    public IPage<UserDealDetail> getUserDealDetailsList(Long userId, Page<UserDealDetail> page) {
        Final<Long> shopIdRef = new Final<>(null);
        ISecurity.match()
                .ifAnySuperAdmin(secureUser -> shopIdRef.set(0L))
                .ifAnyShopAdmin(secureUser -> shopIdRef.set(secureUser.getShopId()));
        return userDealDetailsService.lambdaQuery()
                .eq(UserDealDetail::getUserId, userId)
                .eq(UserDealDetail::getShopId, shopIdRef.get())
                .orderByDesc(BaseEntity::getCreateTime)
                .page(page);
    }
}
