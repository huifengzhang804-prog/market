package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.user.service.model.dto.RuleJsonDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 用户储蓄规则
 *
 * @author xiaoq
 * @Description UserSavingRule.java
 * @date 2022-08-31 16:38
 */

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_user_saving_rule", autoResultMap = true)
public class UserSavingRule extends BaseEntity {

    /**
     * 储值管理开关 0关闭 1开启
     */
    @TableField("switching")
    private Boolean switching;

    /**
     * 优惠状态 0无优惠 1有优惠
     */
    @TableField("discounts_state")
    private Boolean discountsState;

    /**
     * 存储规则json
     */
    @TableField(value = "rule_json", typeHandler = Fastjson2TypeHandler.class)
    private List<RuleJsonDTO> ruleJson;

}
