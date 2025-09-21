package com.medusa.gruul.common.security.resource.tool.jwt;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.security.model.bean.CacheToken;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.bean.TokenKey;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.resource.exception.SecurityException;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.security.resource.model.SecureUserAuthentication;
import com.medusa.gruul.common.system.model.model.ClientType;
import io.vavr.control.Option;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

/**
 * SecurityContextRepository 用于加载和保存SecurityContext
 *
 * @author 张治保
 * @since 2023/11/3
 */
@Component
@RequiredArgsConstructor
public final class JwtSecurityContextRepository implements SecurityContextRepository {
    private static final Authentication ANONYMOUS = new AnonymousAuthenticationToken("key", "anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
    private static final RequestMatcher LOGOUT_REQUEST_MATCHER = new AntPathRequestMatcher("/logout", "POST");
    private static final String CONTEXT_KEY = JwtSecurityContextRepository.class.getName() + "_C-T-X";
    private final JwtDecoder jwtDecoder;

    @Override
    @SuppressWarnings("deprecation")
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        return loadDeferredContext(requestResponseHolder.getRequest()).get();
    }

    @Override
    public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
        return new JwtDeferredSecurityContext(jwtDecoder, request, LOGOUT_REQUEST_MATCHER.matches(request));
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        if (context.getAuthentication() instanceof SecureUserAuthentication) {
            SecurityContextHolder.setContext(context);
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        return request.getAttribute(CONTEXT_KEY) != null;
    }

    @RequiredArgsConstructor
    public final static class JwtDeferredSecurityContext implements DeferredSecurityContext {

        /**
         * jwt 解码器
         */
        private final JwtDecoder jwtDecoder;
        /**
         * 请求上下文
         */
        private final HttpServletRequest request;
        /**
         * 是否是登出请求
         */
        private final boolean logout;

        /**
         * 是否已生成
         */
        private boolean generated;

        @Override
        public SecurityContext get() {
            Object attribute;
            if (generated && (attribute = request.getAttribute(CONTEXT_KEY)) != null) {
                return (SecurityContext) attribute;
            }
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication authentication = this.getAuthentication(request.getHeader(ISecurity.HEADER));
            context.setAuthentication(authentication);
            generated = true;
            request.setAttribute(CONTEXT_KEY, context);
            return context;
        }

        /**
         * 获取认证信息
         *
         * @param token token
         * @return 认证信息
         */
        private Authentication getAuthentication(String token) {
            if (StrUtil.isEmpty(token)) {
                return ANONYMOUS;
            }
            SecureUserAuthentication authentication = jwtDecoder.authentication(token, logout);
            if (logout) {
                return authentication;
            }
            SecureUser<?> secureUser = authentication.getUser();

            String tokenId = authentication.getTokenId();
            ClientType clientType = secureUser.getClientType();
            Long shopId = secureUser.getShopId();
            Long userId = secureUser.getId();
            //取出用户的 token 信息
            Option<CacheToken> cacheTokenOpt = ISecurity.getCacheToken(new TokenKey(clientType, shopId, userId, tokenId));
            //缓存的 token 为空 说明 所有 token 包含刷新 token 已过期
            if (cacheTokenOpt.isEmpty()) {
                throw SecurityException.of(SecureCodes.REFRESH_TOKEN_EXPIRED);
            }
            CacheToken cacheToken = cacheTokenOpt.get();
            return switch (cacheToken.getState()) {
                // 正常在线 说明 token 有效 直接返回
                case ONLINE -> authentication;
                // 重置用户资料 token标记为已过期 触发用户刷新用户资料
                case REFRESH -> throw SecurityException.of(SecureCodes.TOKEN_EXPIRED);
                // 被踢出 抛出账户过期异常
                case KICK -> {
                    //被踢出 跑出账户过期异常 提醒用户
                    //用户下线 删除 Token
                    ISecurity.offlineUser(clientType, shopId, userId, tokenId);
                    //抛出异常
                    throw SecurityException.of(SecureCodes.ACCOUNT_EXPIRED.msgEx(cacheToken.getMessage()));
                }
            };
        }

        @Override
        public boolean isGenerated() {
            return generated;
        }
    }
}
