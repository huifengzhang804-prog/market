package com.medusa.gruul.addon.platform.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.addon.platform.model.dto.SplashAdDTO;
import com.medusa.gruul.addon.platform.util.PlatformUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisUtil;
import java.time.Duration;
import java.util.Objects;
import java.util.function.BiFunction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

/** 广告展示频次
 * @author jipeng
 * @since 2024/6/24
 */
@Getter
@RequiredArgsConstructor
public enum AdShowFrequencyEnum {
  //每人仅展示一次
  ONLY_ONE(1, "仅展示一次", (splashAdDTO, userId) -> {
    //只允许一次
    String key = PlatformUtil.splashAdUseInfoOnceKey(splashAdDTO.getEndPoint());
    Object o = RedisUtil.getRedisTemplate().opsForHash()
        .get(key,
            userId);
    if (Objects.isNull(o)) {
      //第一次看到广告
      RedisUtil.executePipelined((RedisTemplate<String, Object> redisTemplate) -> {
        redisTemplate.opsForHash().increment(key,
              String.valueOf(userId),
              CommonPool.NUMBER_ONE);
        redisTemplate.expire(key,
            Duration.ofDays(CommonPool.NUMBER_ONE));
      });
      return splashAdDTO;
    }else {
      return null;
    }
  }),
  //每次进入页面展示
  EVERY_ENTER(2,"每次进入页面展示",(splashAdDTO, userId) -> splashAdDTO),
  //每日前几次展示
  FEW_TIMES_BEFORE_DAY(3,"每日前几次展示",(splashAdDTO, userId) -> {
    //每日前几次
    String key = PlatformUtil.splashAdUseInfoDayMultiKey(splashAdDTO.getEndPoint());
    Object o = RedisUtil.getRedisTemplate().opsForHash()
        .get(key,
            String.valueOf(userId));
    if (Objects.isNull(o)||Integer.valueOf(o.toString())<splashAdDTO.getTimes()) {
      //没看过广告 或者观看次数小于广告次数
      RedisUtil.executePipelined((RedisTemplate<String, Object> redisTemplate) -> {
        redisTemplate.opsForHash()
            .increment(key,
                String.valueOf(userId),
                CommonPool.NUMBER_ONE);
        redisTemplate.expire(key,
            Duration.ofDays(CommonPool.NUMBER_ONE));
      });

      return splashAdDTO;
    }else {
      return null;
    }
  }),



      ;

  @EnumValue
  private final Integer value;

  private final String desc;

  private final BiFunction<SplashAdDTO,Long,SplashAdDTO> checkShowFun;


}
