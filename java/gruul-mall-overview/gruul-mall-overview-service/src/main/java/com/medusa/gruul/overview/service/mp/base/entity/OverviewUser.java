package com.medusa.gruul.overview.service.mp.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 用户提现信息关联表
 *
 * @author xiaoq
 * @Description 用户提现信息关联表
 * @date 2023-09-18 17:04
 */
@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName("t_overview_user")
public class OverviewUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;
}
