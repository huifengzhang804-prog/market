package com.medusa.gruul.addon.store.service.impl;

import com.medusa.gruul.addon.store.constant.ShopStoreConstant;
import com.medusa.gruul.addon.store.model.dto.ShopAssistantDTO;
import com.medusa.gruul.addon.store.model.vo.ShopAssistantVO;
import com.medusa.gruul.addon.store.mp.entity.ShopAssistant;
import com.medusa.gruul.addon.store.mp.entity.ShopStore;
import com.medusa.gruul.addon.store.mp.service.IShopAssistantService;
import com.medusa.gruul.addon.store.mp.service.IShopStoreService;
import com.medusa.gruul.addon.store.service.ManageShopAssistantService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.service.uaa.api.enums.SmsType;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 管理店铺店员服务实现层
 *
 * @author xiaoq
 * @Description 管理店铺店员服务实现层
 * @date 2023-03-14 14:53
 */
@Service
@RequiredArgsConstructor
public class ManageShopAssistantServiceImpl implements ManageShopAssistantService {
    private final IShopAssistantService shopAssistantService;

    private final IShopStoreService shopStoreService;

    private final UaaRpcService uaaRpcService;

    /**
     * 店铺店员新增
     *
     * @param shopAssistant 店铺店员DTO
     * @param shopId        店铺od
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = ShopStoreConstant.SHOP_STORE_ISSUE_LOCK, key = "#shopId+':'+#shopAssistant.assistantPhone")
    public void issueShopAssistant(ShopAssistantDTO shopAssistant, Long shopId) {
        String assistantPhone = shopAssistant.getAssistantPhone();
        boolean isShopAssistantExists = shopAssistantService.lambdaQuery()
                .eq(ShopAssistant::getAssistantPhone, assistantPhone)
                .exists();
        if (isShopAssistantExists) {
            throw new GlobalException("当前手机号已是店员,无法重复添加");
        }

        Long count = shopAssistantService.lambdaQuery().eq(ShopAssistant::getShopId, shopId).count();
        if (count > (long) CommonPool.NUMBER_EIGHT * CommonPool.NUMBER_THREE) {
            throw new GlobalException("店铺店员至多存在25人");
        }

        // 验证手机号验证码是否正确
        uaaRpcService.checkSmsCodeByType(SmsType.SHOP_STORE_FOUND, assistantPhone, shopAssistant.getAssistantPhoneCode());

        // 保存新的店员信息
        ShopAssistant newShopAssistant = new ShopAssistant()
                .setAssistantPhone(assistantPhone)
                .setShopId(shopId);
        shopAssistantService.save(newShopAssistant);

        // (修改/新增)用户角色信息
        uaaRpcService.checkStoreUserByMobile(Collections.singletonList(assistantPhone));
    }

    /**
     * 获取店铺店员列表
     *
     * @return 店铺店员列表
     */
    @Override
    public List<ShopAssistantVO> getShopAssistantList() {
        return shopAssistantService.getShopAssistantList(ISecurity.userMust().getShopId());
    }

    /**
     * 给店员设置门店
     *
     * @param storeId         门店id
     * @param shopAssistantId 店员id
     */
    @Override
    public void setStore(Long storeId, Long shopAssistantId) {
        Long count = shopAssistantService.lambdaQuery()
                .eq(ShopAssistant::getStoreId, storeId)
                .count();
        if (count >= CommonPool.NUMBER_FIVE) {
            throw new GlobalException("每个门店只能设置五个店员");
        }
        Optional<ShopAssistant> shopAssistantOptional = Optional.ofNullable(shopAssistantService.getById(shopAssistantId));
        ShopAssistant shopAssistant = shopAssistantOptional.orElseThrow(() -> new GlobalException("当前店员不存在"));

        shopAssistant.setStoreId(storeId);
        shopAssistantService.updateById(shopAssistant);
    }

    /**
     * 删除店员
     *
     * @param shopAssistantId 店员id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delShopAssistant(Long shopAssistantId) {
        Optional<ShopAssistant> shopAssistantOptional = Optional.ofNullable(shopAssistantService.getById(shopAssistantId));
        ShopAssistant shopAssistant = shopAssistantOptional.orElseThrow(() -> new GlobalException("当前店员不存在"));
        Long storeId = shopAssistant.getStoreId();
        if (storeId != null) {
            Long count = shopAssistantService.lambdaQuery()
                    .eq(ShopAssistant::getStoreId, storeId)
                    .eq(ShopAssistant::getShopId, ISecurity.userMust().getShopId())
                    .ne(BaseEntity::getId, shopAssistantId)
                    .count();
            if (count < CommonPool.NUMBER_ONE) {
                ShopStore shopStore = shopStoreService.getById(storeId);
                if (shopStore != null) {
                    throw new GlobalException("当前门店只存在当前一个店员,请先给门店添加其他店员或删除门店");
                }
            }
        }

        String assistantPhone = shopAssistant.getAssistantPhone();
        if (!uaaRpcService.delUserRoleByMobiles(assistantPhone, Roles.SHOP_STORE)) {
            throw new GlobalException("删除门店角色失败,请稍后再试");
        }
        shopAssistantService.removeById(shopAssistantId);
    }

}
