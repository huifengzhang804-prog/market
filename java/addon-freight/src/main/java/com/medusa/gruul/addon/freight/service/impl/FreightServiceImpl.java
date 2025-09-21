package com.medusa.gruul.addon.freight.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.freight.constant.FreightConstant;
import com.medusa.gruul.addon.freight.model.dto.FrightConfDTO;
import com.medusa.gruul.addon.freight.model.dto.LogisticsSettingsDTO;
import com.medusa.gruul.addon.freight.model.enums.LogisticsCompanyStatus;
import com.medusa.gruul.addon.freight.model.param.FrightConfParam;
import com.medusa.gruul.addon.freight.mp.entity.FreightConf;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsSettings;
import com.medusa.gruul.addon.freight.mp.service.IFrightConfService;
import com.medusa.gruul.addon.freight.mp.service.ILogisticsSettingService;
import com.medusa.gruul.addon.freight.service.FreightService;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author xiaoq
 * @Description
 * @date 2022-06-06 10:37
 */
@Service
@RequiredArgsConstructor
public class FreightServiceImpl implements FreightService {
    private final IFrightConfService frightConfService;

    private final ILogisticsSettingService logisticsSettingService;

    /**
     * 物流信息新增
     */
    @Override
    public void addFrightInfo(FrightConfDTO frightConfDTO) {
        FreightConf freightConf = frightConfService.lambdaQuery()
                .eq(FreightConf::getLogisticsCompanyCode, frightConfDTO.getLogisticsCompanyCode())
                .one();
        if (freightConf != null) {
            throw new GlobalException("当前快递公司已存在,不可重复添加");
        }
        freightConf = frightConfDTO.coverFrightConf();
        boolean save = frightConfService.save(freightConf);
        if (!save) {
            throw new GlobalException("物流快递新增失败");
        }
    }

    /**
     * 物流信息修改
     *
     * @param frightConfDTO 物流信息
     */
    @Override
    public void updateFrightInfo(FrightConfDTO frightConfDTO) {
        FreightConf freightConf = frightConfDTO.coverFrightConf();
        FreightConf freightConfInfo = frightConfService.getById(freightConf.getId());
        if (freightConfInfo == null) {
            throw new GlobalException("当前物流快递不存在");
        }
        boolean update = frightConfService.updateById(freightConf);
        if (!update) {
            throw new GlobalException("物流快递修改失败");
        }
    }

    /**
     * 物流信息批量删除
     *
     * @param ids 物流快递ids
     */
    @Override
    public void deleteFrightInfo(Long[] ids) {
        boolean remove = frightConfService.lambdaUpdate()
                .in(BaseEntity::getId, Arrays.asList(ids))
                .set(BaseEntity::getDeleted, Boolean.FALSE).remove();
        if (!remove) {
            throw new GlobalException("物流快递删除失败");
        }
    }

    /**
     * 物流信息批量操作
     *
     * @param ids                    物流信息ids
     * @param logisticsCompanyStatus 物流状态
     */
    @Override
    public void batchForbiddenFright(Long[] ids, LogisticsCompanyStatus logisticsCompanyStatus) {
        boolean update = frightConfService.lambdaUpdate()
                .in(BaseEntity::getId, Arrays.asList(ids))
                .set(FreightConf::getLogisticsCompanyStatus, logisticsCompanyStatus).update();
        if (!update) {
            throw new GlobalException("批量禁用失败");
        }
    }

    /**
     * 物流信息list
     *
     * @param frightConfParam param
     * @return 分页<物流信息>
     */
    @Override
    public IPage<FreightConf> getFrightInfoList(FrightConfParam frightConfParam) {
        ClientType clientType = ISystem.clientTypeMust();
        return TenantShop.disable(() -> frightConfService
                .lambdaQuery()
                .eq(clientType != ClientType.PLATFORM_CONSOLE, FreightConf::getLogisticsCompanyStatus, LogisticsCompanyStatus.OPEN)
                .orderByAsc(BaseEntity::getCreateTime)
                .page(frightConfParam));
    }

    /**
     * 新增或修改物流设置dto
     *
     * @param logisticsSettingsDTO 物流快递设置dto
     */
    @Override
    @Redisson(name = FreightConstant.KUAIDI_100_LOCK, key = "@S.matcher().user.shopId")
    public void addLogisticsSettings(LogisticsSettingsDTO logisticsSettingsDTO) {
        //历史配置
        LogisticsSettings one = logisticsSettingService.lambdaQuery()
                .select(BaseEntity::getId)
                .one();
        //配置不存在 新增
        LogisticsSettings logisticsSetting = logisticsSettingsDTO.coverLogisticsSettings();
        if (one == null) {
            logisticsSetting.setId(null);
            logisticsSettingService.save(logisticsSetting);
            return;
        }
        //配置存在 修改
        logisticsSetting.setId(one.getId());
        logisticsSettingService.updateById(logisticsSetting);
    }

    /**
     * 物流设置信息
     *
     * @return LogisticsSettings.java
     */
    @Override
    public LogisticsSettings getLogisticsSettings() {
        return logisticsSettingService.lambdaQuery()
                .select(
                        BaseEntity::getId,
                        LogisticsSettings::getCustomer,
                        LogisticsSettings::getKey,
                        LogisticsSettings::getSecret)
                .one();
    }

    @Override
    public List<FreightConf> getFreightConfByShopId() {
        return frightConfService.lambdaQuery().orderByAsc(BaseEntity::getCreateTime).list();
    }
}
