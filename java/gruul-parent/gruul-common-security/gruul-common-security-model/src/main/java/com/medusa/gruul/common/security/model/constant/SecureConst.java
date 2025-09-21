package com.medusa.gruul.common.security.model.constant;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.system.model.model.ClientType;

import java.util.Set;

/**
 * 常量
 *
 * @author 张治保
 * date 2022/4/19
 */
public interface SecureConst {

    String TOKEN_CACHE_KEY = "gruul:mall:uaa:token:";

    /**
     * 默认的用具昵称前缀
     */
    String DEFAULT_USER_NICKNAME_PREFIX = "U";

    /**
     * 用户登录检查 最后一个参数为shopId 店铺id
     */
    String USER_LOGIN_STATE_CHECK = "gruul:mall:uaa:login:user";

    /**
     * 用户消息模板 最后一个参数为客户端占位符
     */
    String DESTINATION_ALL_USER = "/topic/pigeon-to-{}";

    /**
     * 指定店铺的用户的消息模板
     * 第一个参数为客户端占位符 第二个参数为店铺id占位符
     */
    String DESTINATION_SHOP_USER = DESTINATION_ALL_USER + "-{}";

    /**
     * 指定用户的消息模板
     * 第一个参数为客户端占位符 第二个参数为店铺id占位符 第三个参数为用户id占位符
     */
    String DESTINATION_SHOP_MARKED_USER = DESTINATION_SHOP_USER + "-{}";


    /**
     * 所有的消息模板
     */
    Set<String> ALL_DESTINATION_TEMPLATE = Set.of(
            SecureConst.DESTINATION_ALL_USER,
            SecureConst.DESTINATION_SHOP_USER,
            SecureConst.DESTINATION_SHOP_MARKED_USER
    );

    /**
     * 消息 destination 渲染
     *
     * @param template   destination模板
     * @param clientType 客户端类型
     * @param shopId     店铺id
     * @param userId     用户id
     * @return destination
     */
    static String destination(String template, ClientType clientType, Long shopId, Long userId) {
        return StrUtil.format(template, clientType.name(), shopId, userId);
    }

}
