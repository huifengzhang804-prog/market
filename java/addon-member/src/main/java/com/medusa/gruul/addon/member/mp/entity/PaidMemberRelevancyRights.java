package com.medusa.gruul.addon.member.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 付费会员权益关联
 *
 * @author xiaoq
 * @Description PaidMemberRelevancyRights.java
 * @date 2022-11-15 10:28
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_paid_member_relevancy_rights", autoResultMap = true)
public class PaidMemberRelevancyRights extends BaseEntity {

    /**
     * 付费会员id
     */
    private Long paidMemberId;


    /**
     * 会员权益id
     */
    private Long memberRightsId;

    /**
     * 会员权益扩展值
     */
    private Long extendValue;
}
