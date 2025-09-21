package com.medusa.gruul.overview.service.mp.activity.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.overview.api.enums.ActivityConfigType;
import com.medusa.gruul.overview.api.model.ActivityConfigDTO;
import com.medusa.gruul.overview.service.mp.activity.entity.ActivityConfig;
import org.springframework.stereotype.Service;

/**
 * 活动配类Service接口
 *
 * @author jipeng
 * @since 2024/3/15
 */

public interface IActivityConfigService extends IService<ActivityConfig> {

  /**
   * 根据类型查询活动配置
   * @param type 活动配置类型
   * @return 活动配置信息
   */
  ActivityConfig queryByType(ActivityConfigType type);

  /**
   * 保存活动配置
   * @param activityConfigDTO 活动配置DTO
   */
  void save(ActivityConfigDTO activityConfigDTO);

  /**
   * 更新活动配置
   * @param activityConfigDTO
   */
  void updateConfig(ActivityConfigDTO activityConfigDTO);
}
