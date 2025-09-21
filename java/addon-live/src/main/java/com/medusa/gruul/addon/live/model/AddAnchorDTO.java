package com.medusa.gruul.addon.live.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author miskw
 * @date 2023/6/13
 * @describe 描述
 */
@Getter
@Setter
@Accessors(chain = true)
public class AddAnchorDTO {
    /**
     * 用户id
     */
    @NotNull(message = "用户id不能为空")
    private Long userId;
    /**
     * 主播简介
     */
    private String anchorSynopsis;
}
