package com.medusa.gruul.service.uaa.service.model.po;

import com.medusa.gruul.service.uaa.service.mp.entity.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author 张治保
 * date 2022/4/16
 */
@Getter
@Setter
@Accessors(chain = true)
public class RolesAndPerms {

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 角色集合
     */
    private Set<Role> roles;

    /**
     * 副角色集合
     */
    private Set<Role> subRoles;

    /**
     * 权限列表
     */
    private Set<String> perms;


}
