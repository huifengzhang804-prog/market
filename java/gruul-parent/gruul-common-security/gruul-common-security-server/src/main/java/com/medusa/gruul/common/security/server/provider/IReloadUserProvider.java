package com.medusa.gruul.common.security.server.provider;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.exception.SecurityException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * 重置用户 TOKEN 提供器
 *
 * @author 张治保
 * @since 2023/11/16
 */
public interface IReloadUserProvider {

    /**
     * 根据用户id加载用户信息
     *
     * @param preUser 加载之前的用户认证资料
     * @return 用户信息
     */
    SecureUser<?> loadUser(SecureUser<?> preUser);

    @ConditionalOnMissingBean(IReloadUserProvider.class)
    class DefaultReloadUserProvider implements IReloadUserProvider {

        @Override
        public SecureUser<?> loadUser(SecureUser<?> preUser) {
            throw SecurityException.of(SecureCodes.REFRESH_TOKEN_EXPIRED);
        }

    }
}
