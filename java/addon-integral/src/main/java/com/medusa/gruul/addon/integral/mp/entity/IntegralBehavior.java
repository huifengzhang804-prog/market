package com.medusa.gruul.addon.integral.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.integral.model.enums.GainRuleType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * 积分行为
 *
 * @author xiaoq
 * @Description IntegralBehavior.class
 * @date 2023-02-06 15:32
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("t_integral_behavior")
public class IntegralBehavior extends BaseEntity {
    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 规则类型
     */
    @TableField(value = "rule_type")
    private GainRuleType ruleType;

    /**
     * 当前日期(yyyyMMdd)
     */
    @TableField(value = "`current_date`")
    private LocalDate currentDate;

    /**
     * 连续天数
     */
    @TableField(value = "continue_days")
    private Integer continueDays;

}
