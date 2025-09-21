package com.medusa.gruul.addon.freight.model.vo;

import com.medusa.gruul.addon.freight.model.enums.ValuationModel;
import lombok.Data;

import java.util.List;

/**
 *
 * 物流运费模板Vo
 *
 * @author xiaoq
 * @Description 物流运费模板Vo
 * @date 2022-05-30 10:35
 */
@Data
public class LogisticsTemplateVO {

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
    private List<LogisticsBaseModelVO> logisticsBaseModelVos;

    /**
     * 运费模板包邮设置信息VO
     */
    private List<LogisticsIncludePostageVO> logisticsIncludePostageVos;

}
