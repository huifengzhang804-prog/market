package com.medusa.gruul.live.service.model.dto;

import com.medusa.gruul.live.api.enums.LiveRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @author miskw
 * @date 2022/11/11
 * @describe 接收添加主播参数
 */
@Data
public class RoleDto {
    @NotNull(message = "请填写微信号")
    private String username;

    /**
     * 取值[1-管理员，2-主播，3-运营者]，设置超级管理员将无效
     */
    @NotNull(message = "请选择用户的角色")
    private LiveRole role;

}
