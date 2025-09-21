package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.user.api.enums.MemberCardStatus;
import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.api.enums.OpenType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 用户会员卡
 *
 * @author xiaoq
 * @Description UserMemberCard.java
 * @date 2022-11-17 09:56
 */

@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_user_member_card", autoResultMap = true)
public class UserMemberCard extends BaseEntity {

    /**
     * 会员级别id
     */
    private Long memberId;

    /**
     * 会员等级
     */
    private Integer rankCode;


    /**
     * 用户id
     */
    private Long userId;


    /**
     * 会员类型
     */
    private MemberType memberType;


    /**
     * 会员卡有效时长
     */
    private LocalDate memberCardValidTime;

    /**
     * 会员卡状态
     */
    private MemberCardStatus memberCardStatus;

    /**
     * 开卡方式
     */
    private OpenType openType;


    @TableField(exist = false)
    private String memberName;

}
