package com.medusa.gruul.addon.freight.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.freight.model.dto.FrightConfDTO;
import com.medusa.gruul.addon.freight.model.dto.LogisticsSettingsDTO;
import com.medusa.gruul.addon.freight.model.enums.LogisticsCompanyStatus;
import com.medusa.gruul.addon.freight.model.param.FrightConfParam;
import com.medusa.gruul.addon.freight.mp.entity.FreightConf;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsSettings;

import java.util.List;

/**
 *
 *
 *
 *
 * @author xiaoq
 * @Description
 * @date 2022-06-06 10:36
 */
public interface FreightService {
    /**
     * 新增物流信息修改
     *
     * @param frightConfDTO 物流信息
     */
    void addFrightInfo(FrightConfDTO frightConfDTO);

    /**
     * 物流信息修改
     *
     * @param frightConfDTO 物流信息
     */
    void updateFrightInfo(FrightConfDTO frightConfDTO);

    /**
     * 批量删除
     *
     * @param ids 物流快递ids
     */
    void deleteFrightInfo(Long[] ids);

    /**
     * 批量修改状态
     *
     * @param ids 物流信息ids
     * @param logisticsCompanyStatus 物流状态
     */
    void batchForbiddenFright(Long[] ids, LogisticsCompanyStatus logisticsCompanyStatus);

    /**
     * 分页查询
     *
     * @param frightConfParam param
     * @return IPage<FrightConf>
     */
    IPage<FreightConf> getFrightInfoList(FrightConfParam frightConfParam);

    /**
     * 新增或修改物流快递设置DTO
     *
     * @param logisticsSettingsDTO 物流快递设置dto
     */
    void addLogisticsSettings(LogisticsSettingsDTO logisticsSettingsDTO);


    /**
     * 获取物流设置信息
     *
     * @return LogisticsSettings.java
     */
    LogisticsSettings getLogisticsSettings();


    /**
     * 获取物流配置信息 by shopId
     *
     * @return List<FreightConf>
     */
    List<FreightConf> getFreightConfByShopId();
}
