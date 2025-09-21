package com.medusa.gruul.service.uaa.service.model.dto;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.security.model.enums.MenuType;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.common.web.valid.annotation.Limit;
import com.medusa.gruul.service.uaa.service.model.enums.UaaError;
import com.medusa.gruul.service.uaa.service.mp.entity.Menu;
import com.medusa.gruul.service.uaa.service.mp.service.IMenuService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collections;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/3/29
 */
@Getter
@Setter
@ToString
public class MenuDTO {

    /**
     * 客户端类型
     */
    @NotNull
    @Limit(value = {"PLATFORM_CONSOLE", "SHOP_CONSOLE"})
    private ClientType clientType;

    /**
     * 父目录id
     */
    private Long parentId;

    /**
     * 是否管理员独占
     */
    private Boolean unshared;

    /**
     * 权限
     */
    private String perm;

    /**
     * icon
     */
    private String icon;

    /**
     * 访问路径 当类型为菜单时不为空
     */
    private String path;

    /**
     * 菜单/目录名称
     */
    @NotBlank
    private String name;


    /**
     * 菜单/目录 缩写名称
     */
    private String abbreviationName;

    /**
     * 类型 0目录 1菜单
     */
    @NotNull
    private MenuType type;

    /**
     * 排序
     */
    @NotNull
    private Integer order;


    public void checkParam(IMenuService menuService, Long currentId) {
        if (getType() == MenuType.MENU) {
            if (StrUtil.isBlank(getPath())) {
                throw UaaError.ACCESS_PATH_CANNOT_BE_EMPTY.exception();
            }
            if (getPerm() == null) {
                throw UaaError.ACCESS_PERM_CANNOT_BE_EMPTY.exception();
            }
        } else {
            this.setPerm(null);
            this.setPath(null);
        }

        /*
         * 检查是否已存在同名或同路径菜单
         */
        Long count = menuService.lambdaQuery()
                .ne(currentId != null, Menu::getId, currentId)
                .eq(Menu::getClientType, getClientType())
                .and(
                        query ->
                                query.eq(Menu::getName, getName())
                                        .or(getType() == MenuType.MENU, orQuery -> orQuery.eq(Menu::getPath, getPath()))
                ).count();
        if (count != null && count > 0) {
            throw UaaError.MENU_NAME_OR_PATH_HAS_EXIST.exception();
        }
    }

    /**
     * 获取parentid
     */
    public Long getParentId() {
        return parentId == null ? 0L : parentId;
    }

    /**
     * 渲染为perm渲染为集合
     */
    public Set<String> getPerm() {
        return StrUtil.isBlank(perm) ? null : Collections.singleton(perm);
    }
}
