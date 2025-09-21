package com.medusa.gruul.common.security.resource.config;

import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.context.SystemContextHolder;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.common.system.model.model.Systems;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 有状态token
 *
 * @author 张治保
 */
@RequiredArgsConstructor
public final class ClientAndTokenStateConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenStateHandlerInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/error");
    }

    public final static class TokenStateHandlerInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
            //用户的登录信息
            SecureUser<?> user = ISecurity.secureUser();
            if (user.isAnonymous()) {
                return true;
            }
            //请求头店铺id
            Systems systems = SystemContextHolder.get();
            this.checkSystems(systems);
            //用户登录信息的店铺id
            Long shopId = user.getShopId();
            //检查客户端状态
            ClientType clientType = systems.getClientType();
            checkClientData(clientType, systems.getShopId(), shopId);
            //检查用户登录状态
            return true;
        }

        private void checkSystems(Systems systems) {
            if (systems == null || systems.getShopId() == null || systems.getClientType() == null) {
                throw SecureCodes.REQUEST_INVALID.exception();
            }
        }

        /**
         * 检查客户端数据
         */
        private void checkClientData(ClientType clientType, Long headerShopId, Long userShopId) {
            if (clientType.getShopIdCheck().apply(headerShopId, userShopId)) {
                return;
            }
            throw SecureCodes.REQUEST_INVALID.exception();
        }
    }
}