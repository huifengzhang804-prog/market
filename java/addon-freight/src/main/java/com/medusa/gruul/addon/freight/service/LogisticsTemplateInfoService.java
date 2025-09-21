package com.medusa.gruul.addon.freight.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.freight.model.dto.LogisticsTemplateDTO;
import com.medusa.gruul.addon.freight.model.param.LogisticsTemplateParam;
import com.medusa.gruul.addon.freight.model.vo.LogisticsTemplateVO;
import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;

import java.math.BigDecimal;
import java.util.Map;

/**
 *
 *
 *
 *
 * @author xiaoq
 * @Description
 * @date 2022-05-27 17:17
 */
public interface LogisticsTemplateInfoService {
    /**
     * 物流模板获取
     *
     * @param logisticsTemplateParam 物流模板查询param
     * @return   IPage<LogisticsTemplateVO>
     */
    IPage<LogisticsTemplateVO> getTemplateList(LogisticsTemplateParam logisticsTemplateParam);

    /**
     * 新增物流模板
     *
     * @param logisticsTemplateDto 物流模板dto
     */
    void saveTemplateInfo(LogisticsTemplateDTO logisticsTemplateDto);

    /**
     * 物流模板修改
     *
     * @param logisticsTemplateDto 物流模板dto
     */
    void updateTemplateInfo(LogisticsTemplateDTO logisticsTemplateDto);

    /**
     * 物流模板删除
     *
     * @param id 模板id
     */
    void removeTemplateInfo(Long id);

    /**
     * 获取物流模板 By id
     *
     * @param id  物流模板id
     * @return LogisticsTemplateVO
     */
    LogisticsTemplateVO getTemplateInfo(Long id);

    /**
     * 运费计算
     *
     * @param platformFreight 运费计算param
     * @return Map<shopId+":"+logisticsId,运费金额(当前运费模板)>
     */
    Map<String, BigDecimal> freightCalculation(PlatformFreightParam platformFreight);
}
