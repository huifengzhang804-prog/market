package com.medusa.gruul.addon.freight.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.freight.model.param.LogisticsTemplateParam;
import com.medusa.gruul.addon.freight.model.vo.LogisticsTemplateVO;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsTemplate;

/**
 *
 *
 *
 *
 * @author xiaoq
 * @Description
 * @date 2022-05-27 17:25
 */
public interface ILogisticsTemplateService extends IService<LogisticsTemplate> {

    /**
     * 物流运费模板获取
     *
     * @param logisticsTemplateParam 查询Param
     * @return 物流运费模板Vo
     */
    IPage<LogisticsTemplateVO> getTemplateList(LogisticsTemplateParam logisticsTemplateParam);

    /**
     * 物流运费模板获取 by id
     *
     * @param id 物流模板id
     * @return 物流运费模板Vo
     */
    LogisticsTemplateVO getTemplateInfo(Long id);
}
