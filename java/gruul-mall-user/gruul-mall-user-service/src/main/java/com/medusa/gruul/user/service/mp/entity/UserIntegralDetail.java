package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.user.api.enums.integral.GainIntegralType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 用户积分明细
 *
 * @author xiaoq
 * @Description 用户积分明细
 * @date 2023-02-01 16:04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_user_integral_detail", autoResultMap = true)
public class UserIntegralDetail extends BaseEntity {

    /**
     * 用户id
     */
    private Long userId;


    /**
     * 获取积分类型
     */
    private GainIntegralType gainIntegralType;

    /**
     * 变化的积分值
     */
    private Long variationIntegral;

    /**
     * 变化类型
     */
    private ChangeType changeType;


    /**
     * 积分值详情(说明)
     */
    private  String particulars;

    /**
     * 当前积分值(预留字段)
     */
    private Long currentIntegral;

    /**
     *是否为清空的积分值
     */
    @TableField("is_clear")
    private  Boolean clear;
}
