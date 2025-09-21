package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.user.api.enums.RightsType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 会员权益entity
 *
 * @author xiaoq
 * @ description MemberRights.java
 * @date 2022-11-09 14:13
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_member_rights", autoResultMap = true)
public class MemberRights extends BaseEntity {

    /**
     * 权益类型
     */
    private RightsType rightsType;

    /**
     * 权益名称
     */
    private String rightsName;

    /**
     * 权益图标
     */
    private String rightsIcon;

    /**
     * 权益说明
     */
    private String rightsExplain;

    /**
     * 权益开关
     */
    private Boolean rightsSwitch;


}
