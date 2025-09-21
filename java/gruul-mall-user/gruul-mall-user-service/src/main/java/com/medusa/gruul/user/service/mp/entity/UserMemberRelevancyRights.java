package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 *
 * 免费会员权益关联表entity
 *
 * @author xiaoq
 * @Description UserMemberRelevancyRights.java
 * @date 2022-11-11 09:52
 */

@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_user_member_relevancy_rights", autoResultMap = true)
public class UserMemberRelevancyRights extends BaseEntity {

    /**
     * 用户免费会员id
     */
    private Long memberId;


    /**
     * 会员权益id
     */
    private Long memberRightsId;

    /**
     * 会员权益扩展值
     */
    private Long extendValue;
}
