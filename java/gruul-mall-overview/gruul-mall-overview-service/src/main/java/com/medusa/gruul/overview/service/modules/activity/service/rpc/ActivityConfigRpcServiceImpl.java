package com.medusa.gruul.overview.service.modules.activity.service.rpc;

import com.google.common.collect.Lists;
import com.medusa.gruul.overview.api.enums.ActivityConfigType;
import com.medusa.gruul.overview.api.exception.RemoteResult;
import com.medusa.gruul.overview.api.model.ActivityConfigDTO;
import com.medusa.gruul.overview.api.rpc.ActivityConfigRpcService;
import com.medusa.gruul.overview.service.mp.activity.entity.ActivityConfig;
import com.medusa.gruul.overview.service.mp.activity.service.IActivityConfigService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

/**
 * 活动配置RPC接口实现类
 *
 * @author jipeng
 * @since 2024/3/15
 */
@Service
@DubboService
@Slf4j
@RequiredArgsConstructor
public class ActivityConfigRpcServiceImpl implements ActivityConfigRpcService {

  private final IActivityConfigService activityConfigService;
  @Override
  public RemoteResult<Void> saveOrUpdateConfig(ActivityConfigDTO activityConfigDTO) {
     try {
       ActivityConfig activityConfig=activityConfigService.queryByType(activityConfigDTO.getType());
       if (Objects.isNull(activityConfig)) {
         //新增
         activityConfigService.save(activityConfigDTO);
       }else {
         //更新
         activityConfigService.updateConfig(activityConfigDTO);
       }
     }catch (Exception e){
       e.printStackTrace();
       return RemoteResult.error("接口调用失败",e);
     }
     return RemoteResult.success();

  }

  /**
   * 根据类型查询活动信息
   * @param activityConfigType
   * @return
   */
  @Override
  public RemoteResult<ActivityConfigDTO> queryActivityConfigByType(
      ActivityConfigType activityConfigType) {
    ActivityConfig activityConfig = activityConfigService.queryByType(activityConfigType);
    if (Objects.isNull(activityConfig)) {
      return RemoteResult.success();
    }
    ActivityConfigDTO result= new ActivityConfigDTO();
    result.setType(activityConfigType).setContent(activityConfig.getContent());
    return RemoteResult.success(result);
  }
}
