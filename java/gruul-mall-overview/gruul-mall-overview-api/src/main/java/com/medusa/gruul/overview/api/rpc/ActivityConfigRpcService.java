package com.medusa.gruul.overview.api.rpc;

import com.medusa.gruul.overview.api.enums.ActivityConfigType;
import com.medusa.gruul.overview.api.exception.RemoteResult;
import com.medusa.gruul.overview.api.model.ActivityConfigDTO;

/**
 * 活动配置的RPC接口
 *
 * @author jipeng
 * @since 2024/3/15
 */
public interface ActivityConfigRpcService {

  /**
   * 保存或更新活动配置
   * @param activityConfigDTO
   * @return RPC调用结果
   */
   RemoteResult<Void> saveOrUpdateConfig(ActivityConfigDTO activityConfigDTO);

  /**
   * 根据活动配置类型查询活动
   * @param activityConfigType
   * @return 活动配置信息
   */
   RemoteResult<ActivityConfigDTO> queryActivityConfigByType(ActivityConfigType activityConfigType);
}
