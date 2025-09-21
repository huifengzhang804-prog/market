package com.medusa.gruul.addon.freight.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.freight.model.param.LogisticsTemplateParam;
import com.medusa.gruul.addon.freight.model.vo.LogisticsTemplateVO;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsTemplate;
import com.medusa.gruul.addon.freight.mp.mapper.LogisticsTemplateMapper;
import com.medusa.gruul.addon.freight.mp.service.ILogisticsTemplateService;
import org.springframework.stereotype.Service;

/**
 *
 * @author xiaoq
 * @Description LogisticsTemplateServiceImpl.java
 * @date 2022-05-30 13:15
 */
@Service
public class LogisticsTemplateServiceImpl extends ServiceImpl<LogisticsTemplateMapper, LogisticsTemplate> implements ILogisticsTemplateService {
    /**
     * 物流运费模板获取
     *
     * @param logisticsTemplateParam 查询Param
     * @return 物流运费模板Vo
     */
    @Override
    public IPage<LogisticsTemplateVO>  getTemplateList(LogisticsTemplateParam logisticsTemplateParam) {
        return this.baseMapper.queryTemplateList(logisticsTemplateParam);
    }

    @Override
    public LogisticsTemplateVO getTemplateInfo(Long id) {
        return this.baseMapper.getTemplateInfoById(id);
    }
}
