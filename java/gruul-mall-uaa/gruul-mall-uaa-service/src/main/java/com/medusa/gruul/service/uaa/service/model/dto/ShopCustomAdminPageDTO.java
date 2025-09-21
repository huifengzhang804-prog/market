package com.medusa.gruul.service.uaa.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.service.uaa.service.model.vo.ShopUserDataVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2022/4/27
 */
@Getter
@Setter
@ToString
public class ShopCustomAdminPageDTO extends Page<ShopUserDataVO> {

    /**
     * 当前用户id
     */
    private Long currentUserId;

    /**
     * 关键词 手机号 用户名
     */
    private String keywords;

    /**
     * 角色
     */
    private Roles roles;
    /**
     *昵称
     */
    private String nickname;
    /**
     * 手机号
     */
    private String mobile;

    private Boolean enable;


}
