package com.medusa.gruul.service.uaa.service.model.vo;

import com.medusa.gruul.common.security.model.enums.Roles;
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
public class RoleVO {
    /**
     * id
     */
    private Long id;
    /**
     * 角色
     */
    private Roles value;
    /**
     * 名称
     */
    private String name;

    private Boolean enable;
}
