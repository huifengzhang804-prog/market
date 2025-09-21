package com.medusa.gruul.addon.integral.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.integral.model.dto.RulesParameterDTO;
import com.medusa.gruul.addon.integral.model.enums.GainRuleType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 积分规则表
 *
 * @author xiaoq
 * @Description 积分规则表
 * @date 2023-02-02 11:35
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "t_integral_rules", autoResultMap = true)
public class IntegralRules extends BaseEntity {

    /**
     * 定时任务id
     */
    private Integer taskId;

    /**
     * 积分使用规则
     */
    private String useRule;

    /**
     * 积分有效期
     */
    private Integer indate;

    /**
     * 积分值信息
     */
    private String ruleInfo;

    /**
     * 获取积分规则参数
     */

    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private RulesParameterDTO rulesParameter;

    /**
     * 获取积分规则类型
     */
    private GainRuleType gainRuleType;

    /**
     * 是否开启
     */
    private Boolean open;


}
