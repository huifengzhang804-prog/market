package com.medusa.gruul.service.uaa.service.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.service.uaa.service.mp.entity.UserRole;
import com.medusa.gruul.service.uaa.service.mp.service.IUserRoleService;
import com.medusa.gruul.service.uaa.service.service.LastLoginService;
import com.medusa.gruul.service.uaa.service.service.SecureAuthService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author 张治保
 * date 2023/7/12
 */
@Service
@RequiredArgsConstructor
public class LastLoginServiceImpl implements LastLoginService {

    private final static String KEY_PREFIX = "gruul:mall:uaa:login:last:shop";
    private final IUserRoleService userRoleService;
    private SecureAuthService secureAuthService;

    @Override
    public Long getLastLoginShopId(ClientType clientType, Long userId) {
        Long shopId = RedisUtil.getCacheObject(RedisUtil.key(KEY_PREFIX, clientType, userId));
        if (shopId != null) {
            if (TenantShop.disable(
                    () -> this.userRoleService.lambdaQuery()
                            .exists("SELECT t_role.id from t_role WHERE t_user_role.role_id=t_role.id AND t_role.deleted=0 AND t_role.client_type={0}", clientType.getValue())
                            .eq(UserRole::getEnable, Boolean.TRUE)
                            .eq(UserRole::getUserId, userId)
                            .eq(UserRole::getShopId, shopId)
                            .exists()
            )
            ) {
                return shopId;
            }
        }
        //获取可登录店铺 随机取一个
        List<ShopInfoVO> loginPermShops = secureAuthService.getLoginPermShops(clientType, Set.of(userId));
        return loginPermShops.isEmpty() ? null : loginPermShops.get(RandomUtil.randomInt(loginPermShops.size())).getId();
    }

    @Override
    public void saveLastLoginShopId(ClientType clientType, Long userId, Long shopId) {
        RedisUtil.setCacheObject(
                RedisUtil.key(KEY_PREFIX, clientType, userId),
                shopId,
                CommonPool.NUMBER_TEN,
                TimeUnit.DAYS
        );
    }

    @Override
    public void removeLastLoginShopId(ClientType clientType, Long userId) {
        RedisUtil.delete(RedisUtil.key(KEY_PREFIX, clientType, userId));
    }

    @Lazy
    @Autowired
    public void setSecureAuthService(SecureAuthService secureAuthService) {
        this.secureAuthService = secureAuthService;
    }
}
