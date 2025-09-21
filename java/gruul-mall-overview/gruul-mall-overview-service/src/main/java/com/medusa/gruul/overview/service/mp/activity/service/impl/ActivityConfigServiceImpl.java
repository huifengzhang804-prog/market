package com.medusa.gruul.overview.service.mp.activity.service.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.overview.api.enums.ActivityConfigType;
import com.medusa.gruul.overview.api.model.ActivityConfigDTO;
import com.medusa.gruul.overview.service.mp.activity.entity.ActivityConfig;
import com.medusa.gruul.overview.service.mp.activity.mapper.ActivityConfigMapper;
import com.medusa.gruul.overview.service.mp.activity.service.IActivityConfigService;
import java.time.Duration;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 活动配置Service类
 *
 * @author jipeng
 * @since 2024/3/15
 */
@Service
@RequiredArgsConstructor
public class ActivityConfigServiceImpl  extends ServiceImpl<ActivityConfigMapper, ActivityConfig> implements
    IActivityConfigService {


  /**
   * 根据类型查询活动配置
   * @param type 活动配置类型
   * @return
   */
  @Override
  public ActivityConfig queryByType(ActivityConfigType type) {
    ActivityConfig data = getData(type);
    return data;

  }

  @Override
  public void save(ActivityConfigDTO activityConfigDTO) {
    ActivityConfig config=ActivityConfig.build(activityConfigDTO);
    save(config);
    RedisUtil.delete(activityConfigDTO.getType().getKey());
  }

  @Override
  public void updateConfig(ActivityConfigDTO activityConfigDTO) {
    RedisUtil.doubleDeletion(()-> TenantShop.disable(()->{
      lambdaUpdate().set(ActivityConfig::getContent,activityConfigDTO.getContent())
          .eq(ActivityConfig::getType,activityConfigDTO.getType())
          .update();
    }),activityConfigDTO.getType().getKey());
  }

  private ActivityConfig getData(ActivityConfigType type){

    String cache = RedisUtil.getCache(() -> {
          Object o = RedisUtil.getRedisTemplate().opsForValue().get(type.getKey());
          if (Objects.isNull(o)||StringUtils.equals(o.toString(),StringUtils.EMPTY)) {
           return null;
          }
          return String.valueOf(o);
        }, () -> {
          ActivityConfig activityConfig = TenantShop.disable(() ->
              lambdaQuery().eq(ActivityConfig::getType, type).one());
          if (Objects.isNull(activityConfig)) {
            return StringUtils.EMPTY;
          }
          return JSON.toJSONString(activityConfig);
        }, (data)-> {
          RedisUtil.getRedisTemplate().opsForValue().set(type.getKey(),data, Duration.ofDays(1));
        }
        , RedisUtil.key(type.getKey(), UUID.fastUUID()));
    if (StringUtils.isEmpty(cache)) {
      return null;
    }
    return JSON.parseObject(cache,ActivityConfig.class);
  }
}
