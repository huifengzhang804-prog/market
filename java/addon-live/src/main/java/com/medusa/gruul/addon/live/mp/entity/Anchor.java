package com.medusa.gruul.addon.live.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.live.enums.AnchorStatus;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.service.uaa.api.enums.Gender;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 主播表
 */
@Getter
@Setter
@TableName("t_anchor")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Anchor extends BaseEntity {
    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 主播昵称
     */
    private String anchorNickname;
    /**
     * 主播简介
     */
    private String anchorSynopsis;
    /**
     * 主播头像
     */
    private String anchorIcon;
    /**
     * 主播状态
     */
    @TableField("`status`")
    private AnchorStatus status;
    /**
     * 性别
     */
    private Gender gender;
}
