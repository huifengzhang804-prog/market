package com.medusa.gruul.user.service.service.impl;

import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.user.api.constant.UserConstant;
import com.medusa.gruul.user.service.model.dto.UserGrowthValueSettingsDTO;
import com.medusa.gruul.user.service.mp.entity.UserGrowthValueSettings;
import com.medusa.gruul.user.service.mp.service.IUserGrowthValueSettingsService;
import com.medusa.gruul.user.service.service.UserGrowthValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserGrowthValueServiceImpl implements UserGrowthValueService {

    private final IUserGrowthValueSettingsService userGrowthValueSettingsService;


    /**
     * 获取会员成长值设置
     */
    @Override
    public UserGrowthValueSettings getUserGrowthValueSettings() {
        return userGrowthValueSettingsService.lambdaQuery()
                .select(UserGrowthValueSettings::getId,
                        UserGrowthValueSettings::getRegisterEnabled,
                        UserGrowthValueSettings::getRegisterGrowthValue,
                        UserGrowthValueSettings::getConsumeEnabled,
                        UserGrowthValueSettings::getConsumeJson
                )
                .one();
    }

    /**
     * 更新会员成长值设置
     *
     * @param userGrowthValueSettings 会员成长值设置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = UserConstant.USER_GROWTH_VALUE_KEY)
    public void updateUserGrowthValueSettings(UserGrowthValueSettingsDTO userGrowthValueSettings) {
        UserGrowthValueSettings growthValueSettings = userGrowthValueSettingsService.lambdaQuery().one();
        if (growthValueSettings != null) {
            //更新
            SystemCode.DATA_UPDATE_FAILED.trueThrow(
                    !userGrowthValueSettingsService.lambdaUpdate()
                            .set(UserGrowthValueSettings::getConsumeEnabled, userGrowthValueSettings.getConsumeEnabled())
                            .set(UserGrowthValueSettings::getRegisterEnabled, userGrowthValueSettings.getRegisterEnabled())
                            .set(UserGrowthValueSettings::getRegisterGrowthValue, userGrowthValueSettings.getRegisterGrowthValue())
                            .set(UserGrowthValueSettings::getConsumeJson, JSON.toJSONString(userGrowthValueSettings.getConsumeJson()))
                            .eq(UserGrowthValueSettings::getId, userGrowthValueSettings.getId())
                            .update()
            );
            return;
        }

        //新增
        SystemCode.DATA_ADD_FAILED.trueThrow(
                !userGrowthValueSettingsService.save(
                        new UserGrowthValueSettings()
                                .setConsumeEnabled(userGrowthValueSettings.getConsumeEnabled())
                                .setRegisterEnabled(userGrowthValueSettings.getRegisterEnabled())
                                .setRegisterGrowthValue(userGrowthValueSettings.getRegisterGrowthValue())
                                .setConsumeJson(userGrowthValueSettings.getConsumeJson().stream().peek(item -> item.setId(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(item).longValue())).toList())
                )
        );
    }
}
