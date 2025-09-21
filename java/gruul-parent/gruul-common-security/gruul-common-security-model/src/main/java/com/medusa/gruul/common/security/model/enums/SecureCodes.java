package com.medusa.gruul.common.security.model.enums;

import com.medusa.gruul.common.security.model.constant.SecureCode;
import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/4/20
 */
@RequiredArgsConstructor
public enum SecureCodes implements Error {
    /**
     * 需要登录
     */
    NEED_LOGIN(SecureCode.NEED_LOGIN, "auth.need.login"),
    /**
     * token不可用
     */
    TOKEN_INVALID(SecureCode.TOKEN_INVALID, "auth.token.invalid"),
    /**
     * token已过期
     */
    TOKEN_EXPIRED(SecureCode.TOKEN_EXPIRED, "auth.token.expired"),
    /**
     * refresh token不可用
     */
    REFRESH_TOKEN_INVALID(SecureCode.REFRESH_TOKEN_INVALID, "auth.refresh.token.invalid"),
    /**
     * refresh token 已过期
     */
    REFRESH_TOKEN_EXPIRED(SecureCode.REFRESH_TOKEN_EXPIRED, "auth.refresh.token.expired"),
    /**
     * 权限不足
     */
    PERMISSION_DENIED(SecureCode.PERMISSION_DENIED, "auth.access.denied"),

    /**
     * 账号已过期（账号数据变更） 尝试重新登录
     */
    ACCOUNT_EXPIRED(SecureCode.ACCOUNT_EXPIRED, "auth.account.expired"),
    /**
     * 密码有误
     */
    USERNAME_PASSWORD_INVALID(SecureCode.USERNAME_PASSWORD_INVALID, "auth.username.password.invalid"),
    /**
     * TOKEN 发生变化 已在其它地方登录
     */
    TOKEN_CHANGED(SecureCode.TOKEN_CHANGED, "auth.token.changed"),
    /**
     * scope invalid
     */
    SCOPE_INVALID(SecureCode.SCOPE_INVALID, "auth.scope.invalid"),
    /**
     * REQUEST_INVALID
     */
    REQUEST_INVALID(SecureCode.REQUEST_INVALID, "auth.request.invalid"),
    /**
     * UNSUPPORTED_RESPONSE_TYPE
     */
    RESPONSE_TYPE_INVALID(SecureCode.RESPONSE_TYPE_INVALID, "auth.response.type.invalid"),
    /**
     * 无访问权限
     */
    ACCESS_DENIED(SecureCode.ACCESS_DENIED, "auth.access.denied"),
    /**
     * 重定向地址不可用
     */
    REDIRECT_URI_INVALID(SecureCode.REDIRECT_URI_INVALID, "auth.redirect.uri.invalid"),
    /**
     * 授权不可用
     */
    GRANT_INVALID(SecureCode.GRANT_INVALID, "auth.grant.invalid"),
    /**
     * 账号不可用
     */
    ACCOUNT_INVALID(SecureCode.ACCOUNT_INVALID, "auth.account.invalid"),
    /**
     * 服务不可用
     */
    AUTH_SERVER_ERROR(SecureCode.AUTH_SERVER_ERROR, "auth.server.error"),
    /**
     * 票据已过期
     */
    CREDENTIALS_EXPIRED(SecureCode.CREDENTIALS_EXPIRED, "auth.credentials.expired"),
    /**
     * 数据校验失败
     */
    DATA_VALID_ERROR(SecureCode.DATA_VALID_ERROR, "auth.data.valid.error"),
    /**
     * 未分配权限
     */
    UNALLOCATED_PERMISSION(SecureCode.UNALLOCATED_PERMISSION, "auth.unallocated.permission"),
    ;

    private final int code;
    private final String msg;

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {
        return I18N.msg(msg);
    }
}
