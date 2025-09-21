package com.medusa.gruul.common.security.server.provider;

import com.medusa.gruul.common.security.model.bean.SecureUser;

import java.util.Map;

/**
 * 认证成功后响应的的开放数据解析器
 *
 * @author 张治保
 * @since 2024/6/14
 */
public interface OpenDataResolver<T> {

    /**
     * 渲染开放数据
     *
     * @param secureUser 安全用户
     * @return 开放数据
     */
    Map<String, Object> resolve(SecureUser<T> secureUser);

}
