package com.medusa.gruul.user.service.service;

import com.medusa.gruul.user.service.model.dto.UserGrowthValueSettingsDTO;
import com.medusa.gruul.user.service.mp.entity.UserGrowthValueSettings;

public interface UserGrowthValueService {

    /**
     * 获取会员成长值设置
     * @return 会员成长值设置
     */
    UserGrowthValueSettings getUserGrowthValueSettings();

    /**
     * 更新会员成长值设置
     * @param userGrowthValueSettings 会员成长值设置
     */
    void updateUserGrowthValueSettings(UserGrowthValueSettingsDTO userGrowthValueSettings);
}
