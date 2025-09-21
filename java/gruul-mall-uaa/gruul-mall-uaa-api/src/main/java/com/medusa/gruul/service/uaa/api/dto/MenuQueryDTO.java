package com.medusa.gruul.service.uaa.api.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * @author jipeng
 * @since 2025/4/16
 */
@Data
public class MenuQueryDTO extends Page {
    /**
     * 角色名
     */
    private String name;
    /**
     * 角色状态
     */
    private Boolean enable;
}
