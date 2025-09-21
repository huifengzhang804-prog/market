package com.medusa.gruul.live.api.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * @author miskw
 * @date 2022/11/17
 */
@Data
public class LiveUserDto extends Page<Object> {
    /**
     * 用户昵称和微信号
     */
    private String keywords;
}
