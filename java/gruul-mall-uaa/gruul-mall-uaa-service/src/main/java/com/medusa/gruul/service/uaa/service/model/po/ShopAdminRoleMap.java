package com.medusa.gruul.service.uaa.service.model.po;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * </p>
 *
 * @author 张治保
 * date 2022/5/25
 */
@Getter
@Setter
@ToString
public class ShopAdminRoleMap {

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
}
