package com.medusa.gruul.addon.freight.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.freight.model.param.LogisticsTemplateParam;
import com.medusa.gruul.addon.freight.model.vo.LogisticsTemplateVO;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsTemplate;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author xiaoq
 * @Description LogisticsTemplateMapper.java
 * @date 2022-05-30 13:16
 */
public interface LogisticsTemplateMapper  extends BaseMapper<LogisticsTemplate> {

    /**
     * 分页查询物流运费模板
     *
     * @param logisticsTemplateParam 查询条件param
     * @return IPage< com.medusa.gruul.freight.service.model.vo.LogisticsTemplateVO>
     */
    IPage<LogisticsTemplateVO> queryTemplateList(@Param("logisticsTemplateParam") LogisticsTemplateParam logisticsTemplateParam);

    /**
     * 物流模板获取 by id
     *
     * @param id 物流模板id
     * @return LogisticsTemplateVO
     */
    LogisticsTemplateVO getTemplateInfoById(@Param("id") Long id);
}
