package com.medusa.gruul.addon.freight.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.freight.model.enums.ChoiceConditionPostage;
import com.medusa.gruul.addon.freight.model.enums.ValuationModel;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 物流运费模版基础信息
 *
 * @author xiaoq
 * @Description 物流运费模版基础信息
 * @date 2022-05-26 09:39
 */
@Getter
@Setter
@TableName("t_logistics_template")
public class LogisticsTemplate extends BaseEntity {


    private Long shopId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 计费方式
     */
    private ValuationModel valuationModel;

    /**
     * 是否指定条件包邮: 0=不包邮,1=指定条件包邮
     */
    private ChoiceConditionPostage choiceConditionPostage;
}
