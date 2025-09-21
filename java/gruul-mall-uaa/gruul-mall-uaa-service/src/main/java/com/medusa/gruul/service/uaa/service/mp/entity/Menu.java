package com.medusa.gruul.service.uaa.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.security.model.enums.MenuType;
import com.medusa.gruul.common.system.model.model.ClientType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.Set;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author 张治保
 * @since 2022-02-23
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_menu", autoResultMap = true)
public class Menu extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 是否管理员独占
     */
    private Boolean unshared;

    /**
     * 名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 菜单/目录 缩写名称
     */
    private String abbreviationName;

    /**
     * 图标
     */
    private String icon;

    /**
     * 菜单访问路径
     */
    private String path;

    /**
     * 类型 0目录 1菜单 2API
     */
    private MenuType type;

    /**
     * 0平台端 1商家端
     */
    private ClientType clientType;

    /**
     * 权限列表
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private Set<String> perms;

    /**
     * 排序
     */
    @TableField("`order`")
    private Integer order;


}
