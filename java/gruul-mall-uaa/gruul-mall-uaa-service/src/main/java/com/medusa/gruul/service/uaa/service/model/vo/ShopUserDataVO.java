package com.medusa.gruul.service.uaa.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2022/4/27
 */
@Getter
@Setter
@ToString
public class ShopUserDataVO {
    /**
     * id
     */
    private Long id;

    /**
     * 用户角色信息
     */
    private UserRoleVO userRole;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 版本号
     */
    private Integer version;
}
