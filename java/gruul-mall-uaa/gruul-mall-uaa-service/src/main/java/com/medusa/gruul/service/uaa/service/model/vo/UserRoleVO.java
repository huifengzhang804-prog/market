package com.medusa.gruul.service.uaa.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2022/4/28
 */
@Getter
@Setter
@ToString
public class UserRoleVO {
    /**
     * id
     */
    private Long id;
    /**
     * 是否启用
     */
    private Boolean  enable;

    /**
     * 用户信息
     */
    private UserVO user;
    /**
     * 角色信息
     */
    private RoleVO role;

}
