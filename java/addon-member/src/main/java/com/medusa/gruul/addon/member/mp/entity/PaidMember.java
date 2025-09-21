package com.medusa.gruul.addon.member.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.user.api.model.dto.MemberLabelDTO;
import com.medusa.gruul.user.api.model.dto.paid.PaidRuleJsonDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 付费会员
 *
 * @author xiaoq
 * @Description PaidMember.java
 * @date 2022-11-14 19:21
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_paid_member", autoResultMap = true)
public class PaidMember extends BaseEntity {

    /**
     * 付费会员名称
     */
    private String paidMemberName;

    /**
     * 付费会员级别
     */
    private Integer rankCode;


    /**
     * 付费规则
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<PaidRuleJsonDTO> paidRuleJson;

    /**
     * 标签
     */
    @TableField(value = "label_json", typeHandler = Fastjson2TypeHandler.class)
    private MemberLabelDTO labelJson;

}
