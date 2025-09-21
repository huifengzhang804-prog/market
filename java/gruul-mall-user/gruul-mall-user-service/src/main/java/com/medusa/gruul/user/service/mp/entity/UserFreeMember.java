package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.user.api.model.dto.MemberLabelDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 用户免费会员
 *
 * @author xiaoq
 * @Description UserFreeMember.java
 * @date 2022-11-10 18:26
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_user_free_member", autoResultMap = true)
public class UserFreeMember extends BaseEntity {

    /**
     * 免费会员名称
     */
    private String freeMemberName;

    /**
     * 免费会员级别
     */
    private Integer rankCode;

    /**
     * 所需成长值
     */
    private Long needValue;

    /**
     * 标签
     */
    @TableField(value = "`label_json`", typeHandler = Fastjson2TypeHandler.class)
    private MemberLabelDTO labelJson;

}
