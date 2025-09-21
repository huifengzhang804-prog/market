package com.medusa.gruul.service.uaa.service.strategy;

import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.common.model.base.UserKey;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.server.provider.OpenDataResolver;

import java.util.Map;

/**
 * 开放数据处理器 用于生成开放数据展示给客户端
 *
 * @author 张治保
 * @since 2024/6/14
 */
public class UserKeyOpenDataResolver implements OpenDataResolver<Object> {
    @Override
    public Map<String, Object> resolve(SecureUser<Object> secureUser) {
        return JSONObject.from(
                new UserKey(
                        secureUser.getShopId(),
                        secureUser.getId()
                )
        );
    }
}
