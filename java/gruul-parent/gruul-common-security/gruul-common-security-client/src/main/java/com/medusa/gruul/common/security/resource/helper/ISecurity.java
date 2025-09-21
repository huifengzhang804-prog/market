package com.medusa.gruul.common.security.resource.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.CacheToken;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.bean.TokenKey;
import com.medusa.gruul.common.security.model.constant.SecureConst;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.model.enums.SecureCodes;
import com.medusa.gruul.common.security.model.enums.TokenState;
import com.medusa.gruul.common.security.resource.extension.IUserRolePermMatcher;
import com.medusa.gruul.common.security.resource.extension.RolePermMatcher;
import com.medusa.gruul.common.security.resource.extension.RoleTask;
import com.medusa.gruul.common.security.resource.model.OnlineUserParam;
import com.medusa.gruul.common.system.model.model.ClientType;
import io.vavr.control.Option;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 安全服务工具类
 *
 * @author 张治保
 */
public interface ISecurity {


    /**
     * 匿名访问类型
     */
    Class<? extends Authentication> ANONYMOUS_CLASS = AnonymousAuthenticationToken.class;

    /**
     * token头
     */
    String HEADER = "Authorization";


    /**
     * 在 authentication 下 执行操作逻辑,并返回一个值
     *
     * @param authentication 认证信息
     * @param supplier       执行的任务
     * @return 任务执行结果
     */
    static <T> T withAuthentication(Authentication authentication, Supplier<T> supplier) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        try {
            return supplier.get();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * 在 authentication 下 执行操作逻辑
     *
     * @param authentication 认证信息
     * @param runnable       执行任务
     */
    static void withAuthentication(Authentication authentication, Runnable runnable) {
        ISecurity.withAuthentication(
                authentication,
                () -> {
                    runnable.run();
                    return null;
                }
        );
    }

    /**
     * 获取认证信息
     * 已认证 OAuth2Authentication
     * 未认证  AnonymousAuthenticationToken
     *
     * @param allowAnonymous 是否允许匿名访问
     * @return 认证信息
     */
    static Authentication getAuthentication(boolean allowAnonymous) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!allowAnonymous && isAnonymous(authentication)) {
            throw SecureCodes.NEED_LOGIN.exception();
        }
        return authentication;
    }


    /**
     * 获取可能为空的用户信息
     *
     * @return option<user>
     */
    static <T> Option<SecureUser<T>> userOpt() {
        SecureUser<T> user = ISecurity.secureUser();
        if (user.isAnonymous()) {
            return Option.none();
        }
        return Option.of(user);
    }

    /**
     * 必须要有登陆认证的用户信息
     *
     * @return 当前登陆的用户信息
     */
    static <T> SecureUser<T> userMust() {
        Authentication authentication = getAuthentication(false);
        if (authentication == null) {
            throw SecureCodes.NEED_LOGIN.exception();
        }
        return userMust(authentication);
    }

    /**
     * 必须要有登陆认证的用户信息
     *
     * @param authentication 认证信息
     * @return 当前登陆的用户信息
     */
    static <T> SecureUser<T> userMust(@NonNull Authentication authentication) {
        SecureUser<T> user = ISecurity.matcher(authentication)
                .getUser();
        if (user.isAnonymous()) {
            throw SecureCodes.NEED_LOGIN.exception();
        }
        return user;
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    static <T> SecureUser<T> secureUser() {
        return ISecurity.secureUser(ISecurity.getAuthentication(true));
    }

    /**
     * 根据指定的认证信息 获取当前用户信息
     *
     * @param authentication 认证信息
     * @return 用户信息
     */
    static <T> SecureUser<T> secureUser(@NonNull Authentication authentication) {
        return ISecurity.matcher(authentication)
                .getUser();
    }

    /**
     * 是否是匿名访问
     *
     * @param authentication 认证信息
     * @return 是否是匿名访问
     */
    static boolean isAnonymous(Authentication authentication) {
        return authentication == null || ANONYMOUS_CLASS.isAssignableFrom(authentication.getClass());
    }

    /**
     * 是否是匿名访问
     *
     * @return 是否是匿名访问
     */
    static boolean isAnonymous() {
        return ISecurity.matcher().getUser().isAnonymous();
    }

    /**
     * 是否已认证
     *
     * @return 是否已认证
     */
    static boolean isAuthenticated() {
        return !ISecurity.isAnonymous();
    }

    /**
     * 每个角色对应不同的处理任务
     *
     * @return {@link RoleTask}
     */
    static RoleTask match() {
        return new RoleTask();
    }

    /**
     * 获取 用户与权限匹配器
     *
     * @return 用户与权限匹配器
     */
    static IUserRolePermMatcher matcher() {
        return ISecurity.matcher(ISecurity.getAuthentication(true));
    }

    /**
     * 获取 用户与权限匹配器
     *
     * @param authentication 认证信息
     * @return 用户与权限匹配器
     */
    static IUserRolePermMatcher matcher(Authentication authentication) {
        return new RolePermMatcher(authentication);
    }

    /**
     * 是否匹配到任意角色
     *
     * @param roles 角色列表
     * @return 是否匹配到任意角色
     */
    static boolean anyRole(Roles... roles) {
        return ISecurity.matcher()
                .anyRole(roles)
                .match();
    }


    /**
     * 用户重置 仅刷新当前客户端和店铺 id（用户资料更新 触发用户重新加载个人最新信息 ）
     *
     * @param type    客户端类型
     * @param shopId  店铺 id
     * @param userIds 用户id列表
     */
    static void refreshUsers(ClientType type, Long shopId, Set<Long> userIds) {
        updateUsersState(
                type,
                shopId,
                userIds,
                TokenState.REFRESH,
                StrUtil.EMPTY
        );
    }

    /**
     * 用户重置 重置所有客户端、登录的用户信息 （用户资料更新 触发用户重新加载个人最新信息 ）
     *
     * @param userIds 用户id列表
     */
    static void refreshUsers(Set<Long> userIds) {
        Set<String> keys = RedisUtil.scan(
                userIds.stream()
                        .map(
                                userId -> RedisUtil.key(
                                        SecureConst.USER_LOGIN_STATE_CHECK,
                                        '*',
                                        '*',
                                        userId.toString(),
                                        '*'
                                )
                        ).collect(Collectors.toSet())
        );
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        updateUsersState(keys, TokenState.REFRESH, StrUtil.EMPTY);
    }


    /**
     * 把登录状态的用户踢出
     *
     * @param type    客户端类型
     * @param shopId  店铺id
     * @param userIds 需要踢出的用户id列表
     * @param message 提示信息
     */
    static void kickUsers(ClientType type, Long shopId, Set<Long> userIds, String message) {
        updateUsersState(
                type,
                shopId,
                userIds,
                TokenState.KICK,
                message
        );
    }

    /**
     * 更新用户登录状态状态
     */
    private static void updateUsersState(ClientType type, Long shopId, Set<Long> userIds, TokenState state, String message) {
        Collection<String> keys = allMatchedKeys(type, shopId, userIds);
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        updateUsersState(keys, state, message);
    }

    /**
     * 更新用户登录状态状态
     */
    private static void updateUsersState(Collection<String> keys, TokenState state, String message) {
        Map<String, Object> value = Map.of(
                CacheToken.STATE_FILED, state.name(),
                CacheToken.MESSAGE_FILED, message
        );
        Map<String, Map<String, Object>> keyWithHashValueMap = MapUtil.newHashMap(keys.size());
        for (String key : keys) {
            keyWithHashValueMap.put(key, value);
        }
        RedisUtil.batchPutIfPresent(keyWithHashValueMap);
    }


    /**
     * 批量店铺用户下线
     *
     * @param type    客户端类型
     * @param shopIds 店铺id列表
     */
    static void offlineAllUsers(ClientType type, Set<Long> shopIds) {
        if (CollUtil.isEmpty(shopIds)) {
            return;
        }
        Set<String> patterns = shopIds.stream()
                .map(shopId -> ISecurity.tokenPattern(type, shopId))
                .collect(Collectors.toSet());
        RedisUtil.matchThenDelete(patterns);
    }

    /**
     * 禁用店铺id下的部分指定用户
     *
     * @param type    客户端类型
     * @param shopId  店铺id
     * @param userIds 用户id列表
     */
    static void offlineUsers(ClientType type, Long shopId, Long... userIds) {
        ISecurity.offlineUsers(type, shopId, Set.of(userIds));
    }

    /**
     * 禁用店铺id下的部分指定用户
     *
     * @param type    客户端类型
     * @param shopId  店铺id
     * @param userIds 用户id列表
     */
    static void offlineUsers(ClientType type, Long shopId, Collection<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return;
        }
        RedisUtil.matchThenDelete(
                allUserPatternKeys(type, shopId, userIds)
        );
    }


    /**
     * 具体的 token 下线
     *
     * @param type    客户端类型
     * @param shopId  店铺id
     * @param userId  用户id
     * @param tokenId token id
     */
    static void offlineUser(ClientType type, Long shopId, Long userId, String tokenId) {
        offlineUser(new TokenKey(type, shopId, userId, tokenId));
    }

    /**
     * 具体的 token 下线
     *
     * @param key token key
     */
    static void offlineUser(TokenKey key) {
        RedisUtil.delete(ISecurity.tokenKey(key));
    }

    /**
     * 获取所有匹配到的用户 TokenKey
     *
     * @param type    客户端类型
     * @param shopId  店铺id
     * @param userIds 用户id列表
     * @return 匹配到的所有key
     */
    private static Set<String> allMatchedKeys(ClientType type, Long shopId, Collection<Long> userIds) {
        return RedisUtil.scan(
                allUserPatternKeys(type, shopId, userIds)
        );
    }

    /**
     * 获取所有匹配到的用户 TokenKey
     *
     * @param type    客户端类型
     * @param shopId  店铺id
     * @param userIds 用户id列表
     * @return 匹配到的所有key
     */
    private static Set<String> allUserPatternKeys(ClientType type, Long shopId, Collection<Long> userIds) {
        return userIds.stream()
                .map(userId -> ISecurity.tokenKey(
                        new TokenKey(
                                type,
                                shopId,
                                userId,
                                "*"
                        )
                ))
                .collect(Collectors.toSet());
    }


    /**
     * 新增店铺id下的用户 上线
     *
     * @param onlineUser 用户上线参数
     */
    @SuppressWarnings("unchecked")
    static void onlineUser(OnlineUserParam onlineUser) {
        String tokenKey = ISecurity.tokenKey(
                new TokenKey(
                        onlineUser.getClientType(),
                        onlineUser.getShopId(),
                        onlineUser.getUserId(),
                        onlineUser.getTokenId()
                )
        );
        RedisUtil.executePipelined(
                operations -> {
                    HashOperations<String, String, Object> hashOperations = operations.opsForHash();
                    hashOperations.put(tokenKey, CacheToken.STATE_FILED, TokenState.ONLINE.name());
                    operations.expireAt(tokenKey, onlineUser.refreshTokenExpire());
                }
        );
    }

    /**
     * 获取缓存的 tokenId
     *
     * @param tokenKey token key
     * @return tokenId 可能为空
     */
    static Option<CacheToken> getCacheToken(TokenKey tokenKey) {
        return Option.of(
                RedisUtil.getCacheMap(
                        ISecurity.tokenKey(tokenKey),
                        CacheToken.class
                )
        );
    }

    /**
     * 生成 token key 前缀
     *
     * @param tokenKey token key
     * @return token key 前缀
     */
    static String tokenKey(TokenKey tokenKey) {
        return RedisUtil.key(
                SecureConst.USER_LOGIN_STATE_CHECK,
                tokenKey.getClientType().name(),
                tokenKey.getShopId().toString(),
                tokenKey.getUserId().toString(),
                tokenKey.getTokenId()
        );
    }


    /**
     * 生成 token key 匹配 前缀
     *
     * @param clientType 客户端类型
     * @param shopId     店铺id
     * @return token key 匹配 前缀
     */
    static String tokenPattern(ClientType clientType, Long shopId) {
        return RedisUtil.key(
                SecureConst.USER_LOGIN_STATE_CHECK,
                clientType.name(),
                shopId.toString(),
                "*"
        );
    }

    /**
     * 当前用户名与用户id后六位生成用户昵称
     * 当前用户名不为空 返回当前用户名 否则使用用户id生成昵称
     *
     * @param current 当前用户名
     * @param userId  用户id
     * @return 用户昵称 option
     */
    static Option<String> generateNickName(String current, Long userId) {
        return Option.when(StrUtil.isNotBlank(current), current)
                .orElse(
                        ISecurity.generateNickName(userId)
                );
    }

    /**
     * 根据用户id后六位生成用户昵称
     *
     * @param userId 用户id
     * @return 用户昵称 option
     */
    static Option<String> generateNickName(Long userId) {
        return Option.of(userId)
                .map(id -> {
                    String idStr = String.valueOf(id);
                    return SecureConst.DEFAULT_USER_NICKNAME_PREFIX + StrUtil.subSufByLength(idStr, 6);
                });
    }

    /**
     * user信息转 principal 加密后的用户数据 用户主要信息
     *
     * @param secureUser 用户信息
     * @return principal 加密后的用户数据 用户主要信息
     */
    static byte[] toPrincipal(SecureUser<?> secureUser) {
        return JSONB.toBytes(
                secureUser,
                JSONWriter.Feature.FieldBased,
                JSONWriter.Feature.BeanToArray,
                JSONWriter.Feature.ReferenceDetection,
                JSONWriter.Feature.NotWriteDefaultValue,
                JSONWriter.Feature.NotWriteEmptyArray
        );
    }


    /**
     * principal 加密后的用户数据 用户主要信息
     *
     * @param principal 加密后的用户数据 用户主要信息
     * @return user信息
     */
    static SecureUser<?> toUser(byte[] principal) {
        return JSONB.parseObject(
                principal,
                ExtraRef.get(),
                JSONReader.Feature.FieldBased,
                JSONReader.Feature.SupportArrayToBean,
                JSONReader.Feature.UseDefaultConstructorAsPossible,
                JSONReader.Feature.UseNativeObject
        );
    }


    /**
     * 用于存储、获取额外数据的类型
     */
    class ExtraRef {

        /**
         * 安全用户 额外数据的类型 默认 object 类型
         */
        private static TypeReference<SecureUser<?>> typeReference = new TypeReference<>() {
        };


        /**
         * 设置安全用户额外数据的类型
         *
         * @param clazz 额外数据的类型
         * @param <T>   额外数据的类型 泛型
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        public static <T> void set(Class<T> clazz) {
            typeReference = (TypeReference) new TypeReference<SecureUser<T>>(clazz) {
            };
        }


        /**
         * 获取安全用户额外数据的类型
         *
         * @return TypeReference<SecureUser < ?>>
         */
        public static TypeReference<SecureUser<?>> get() {
            return typeReference;
        }


    }
}

