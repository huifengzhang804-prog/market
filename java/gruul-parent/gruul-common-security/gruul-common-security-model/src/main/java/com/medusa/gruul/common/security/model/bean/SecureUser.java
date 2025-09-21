package com.medusa.gruul.common.security.model.bean;

import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.system.model.model.ClientType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * 安全用户 匿名用户 id为空
 *
 * @author 张治保
 * date 2022/2/26
 */
@Getter
@Setter
@Accessors(chain = true)
public final class SecureUser<T> implements Serializable {

    /**
     * 客户端类型
     */
    private ClientType clientType;

    /**
     * shopId
     */
    private Long shopId;

    /**
     * 用户id 匿名用户id为空
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String mobile;

    /**
     * 邮件
     */
    private String email;

    /**
     * 微信 open id
     */
    private String openid;

    /**
     * 用户角色集合
     */
    private Set<Roles> roles = Set.of();

    /**
     * 副角色
     */
    private Set<Roles> subRoles = Set.of();

    /**
     * 用户权限集合
     */
    private Set<String> perms = Set.of();

    /**
     * 额外信息
     */
    private T extra;

    /**
     * 开放的信息 可用于展示给前端
     */
    private Map<String, Object> open = Map.of();

    /**
     * 是否是匿名用户
     *
     * @return true 是 false 不是
     */
    public boolean isAnonymous() {
        return getId() == null;
    }

    /**
     * 是否是黑名单用户
     */
    public boolean isBlack() {
        return getSubRoles().contains(Roles.BLACK_LIST);
    }

    /**
     * 获取开放数据 并清空
     *
     * @return 开放数据
     */
    public Map<String, Object> open() {
        try {
            return open;
        } finally {
            this.setOpen(null);
        }
    }
}
