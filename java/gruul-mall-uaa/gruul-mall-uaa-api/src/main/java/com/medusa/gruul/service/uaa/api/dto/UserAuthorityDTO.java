package com.medusa.gruul.service.uaa.api.dto;

import com.medusa.gruul.common.security.model.enums.Roles;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Set;

/**
 * 用户权限设置dto
 *
 * @author xiaoq
 * @Description UserAuthorityDTO.java
 * @date 2022-12-01 16:10
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class UserAuthorityDTO implements Serializable {

    /**
     * 用户id
     */
    @NotNull
    @Size(min = 1)
    private Set<Long> userIds;

    /**
     * 用户权限
     */
    @NotNull
    @Size(min = 1)
    private Set<Roles> roles;


    /**
     * 处理原因
     */
    private String explain;
}
