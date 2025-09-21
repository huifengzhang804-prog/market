package com.medusa.gruul.service.uaa.service.model.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * date 2023/4/21
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class NavigateVO implements Serializable {

    /**
     * 菜单列表
     */
    private List<MenuVO> menus;


    /**
     * 是否是管理员
     */
    private Boolean isAdmin;


}
