package com.medusa.gruul.service.uaa.service.model.po;

import com.medusa.gruul.common.security.model.enums.Roles;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-03-14 21:06
 */
@Getter
@Setter
@ToString
public class UserRoleDetailsInfo {

    /**
     * 用户角色对应关系id
     */
    private Long userRoleId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 角色值信息
     */
    private Roles roles;
}
