package com.medusa.gruul.user.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.user.service.mp.entity.UserAccount;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 用户黑名单查询dto
 *
 * @author xiaoq
 * @Description UserBlacklistQueryDTO.java
 * @date 2022-12-01 17:01
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserBlacklistQueryDTO extends Page<UserAccount> {
    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户权限
     */
    private String roles;
}
