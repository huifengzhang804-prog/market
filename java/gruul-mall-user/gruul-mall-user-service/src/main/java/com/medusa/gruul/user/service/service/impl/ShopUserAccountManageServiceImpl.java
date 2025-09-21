package com.medusa.gruul.user.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.model.OrderCreatedDTO;
import com.medusa.gruul.user.api.constant.UserConstant;
import com.medusa.gruul.user.service.model.dto.ShopUserQueryDTO;
import com.medusa.gruul.user.service.model.vo.ShopUserVO;
import com.medusa.gruul.user.service.mp.entity.ShopUserAccount;
import com.medusa.gruul.user.service.mp.entity.UserAccount;
import com.medusa.gruul.user.service.mp.service.IShopUserAccountService;
import com.medusa.gruul.user.service.mp.service.IUserAccountService;
import com.medusa.gruul.user.service.service.ShopUserAccountManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wudi
 * ShopUserAccountManageServiceImpl.class
 */
@Service
@RequiredArgsConstructor
public class ShopUserAccountManageServiceImpl implements ShopUserAccountManageService {

    private final IUserAccountService userAccountService;
    private final IShopUserAccountService shopUserAccountService;

    /**
     * 分页查询店铺客户列表
     *
     * @param shopUserQuery 店铺客户查询参数
     * @return 店铺客户列表
     */
    @Override
    public IPage<ShopUserVO> getShopUserAccountList(ShopUserQueryDTO shopUserQuery) {
        return shopUserAccountService.getShopUserAccountList(shopUserQuery);
    }

    /**
     * 获取店铺客户详情
     *
     * @param userId 店铺客户id
     * @return 店铺客户详情
     */
    @Override
    public ShopUserVO getShopUserAccountDetail(Long userId) {
        return shopUserAccountService.getShopUserAccountDetail(ISecurity.userMust().getShopId(), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = UserConstant.USER_SHOP_SAVE, key = "#orderCreated.getBuyerId")
    public void addShopUserAccount(OrderCreatedDTO orderCreated) {
        Long buyerId = orderCreated.getBuyerId();
        UserAccount userAccount = userAccountService.lambdaQuery()
                .eq(UserAccount::getUserId, buyerId)
                .one();
        if (userAccount == null) {
            return;
        }
        List<ShopOrderItem> shopOrderItems = orderCreated.getShopOrderItems();
        List<Long> shopIds = shopOrderItems.stream()
                .map(ShopOrderItem::getShopId)
                .toList();
        List<ShopUserAccount> shopUserAccounts = shopIds.stream()
                .distinct()
                .filter(shopId -> !shopUserAccountService.lambdaQuery()
                        .eq(ShopUserAccount::getShopId, shopId)
                        .eq(ShopUserAccount::getUserId, buyerId)
                        .exists()
                ).map(shopId -> new ShopUserAccount()
                        .setUserId(buyerId)
                        .setShopId(shopId)
                        .setGender(userAccount.getGender())
                        .setUserName(userAccount.getUserName())
                        .setUserNickname(userAccount.getUserNickname())
                        .setUserHeadPortrait(userAccount.getUserHeadPortrait())
                        .setUserPhone(userAccount.getUserPhone())
                        .setBalance(userAccount.getBalance())
                        .setGrowthValue(userAccount.getGrowthValue())
                        .setIntegralTotal(userAccount.getIntegralTotal())
                        .setRemark(userAccount.getRemark())
                        .setConsumeCount(userAccount.getConsumeCount())
                        .setShopConsumption(0L)
                        .setBirthday(userAccount.getBirthday())
                ).toList();
        if (CollUtil.isEmpty(shopUserAccounts)) {
            return;
        }
        boolean success = shopUserAccountService.saveBatch(shopUserAccounts);
        if (!success) {
            throw new GlobalException("店铺会员保存失败");
        }
    }
}
