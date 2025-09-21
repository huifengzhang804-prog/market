package com.medusa.gruul.addon.freight.model.dto;

import cn.hutool.core.bean.BeanUtil;
import com.medusa.gruul.addon.freight.model.enums.ValuationModel;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsTemplate;
import lombok.Data;

import java.util.List;

/**
 *
 * @author xiaoq
 * @Description LogisticsTemplateDTO.jva
 * @date 2022-05-30 14:51
 */
@Data
public class LogisticsTemplateDTO {
    private Long id;

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
    private Integer choiceConditionPostage;

    /**
     * 物流基础模板信息VO
     */
    private List<LogisticsBaseModelDTO> logisticsBaseModelDTO;

    /**
     * 运费模板包邮设置信息VO
     */
    private List<LogisticsIncludePostageDTO> logisticsIncludePostageDTO;


    public LogisticsTemplate coverLogisticsTemplate() {
        LogisticsTemplate logisticsTemplate = new LogisticsTemplate();
        BeanUtil.copyProperties(this, logisticsTemplate);
        return logisticsTemplate;
    }
}
