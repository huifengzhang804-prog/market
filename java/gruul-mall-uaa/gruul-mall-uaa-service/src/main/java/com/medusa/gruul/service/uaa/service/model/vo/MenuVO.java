package com.medusa.gruul.service.uaa.service.model.vo;

import com.medusa.gruul.common.security.model.enums.MenuType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/3/3
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class MenuVO implements Serializable {
    /**
     * id
     */
    private Long id;

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
     * 类型 0目录 1菜单
     */
    private MenuType type;

    /**
     * 权限列表 array
     */
    private Set<String> perms;

    /**
     * 排序
     */
    private Integer order;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * 子菜单
     */
    private List<MenuVO> children;

}
