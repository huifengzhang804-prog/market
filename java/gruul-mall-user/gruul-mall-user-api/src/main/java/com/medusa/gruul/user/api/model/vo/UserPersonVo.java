package com.medusa.gruul.user.api.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author miskw
 * @date 2023/5/10
 * @describe 描述
 */
@Data
@Accessors(chain = true)
public class UserPersonVo {
    /**
     * 余额
     */
    private Long balance;
    /**
     * 积分
     */
    private Long integral;
    /**
     *收藏数量
     */
    private Long collectCount;
    /**
     * 足迹
     */
    private Long footprint;
}
