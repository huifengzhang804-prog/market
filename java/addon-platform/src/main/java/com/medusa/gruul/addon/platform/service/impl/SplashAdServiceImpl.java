package com.medusa.gruul.addon.platform.service.impl;

import com.medusa.gruul.addon.platform.model.dto.SplashAdDTO;
import com.medusa.gruul.addon.platform.mp.entity.SplashAd;
import com.medusa.gruul.addon.platform.mp.service.ISplashAdMpService;
import com.medusa.gruul.addon.platform.service.SplashAdService;
import com.medusa.gruul.addon.platform.util.PlatformUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 开屏广告Service
 *
 * @author jipeng
 * @since 2024/6/24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SplashAdServiceImpl implements SplashAdService {

  private final ISplashAdMpService splashAdMpService;

  /**
   * 保存或更新开屏广告
   *
   * @param splashAdDTO 开屏广告DTO
   */
  @Override
  public void saveOrUpdate(SplashAdDTO splashAdDTO) {
    LocalDateTime now = LocalDateTime.now();
    //校验结束时间
    if (now.isAfter(splashAdDTO.getEndTime())) {
      throw SystemCode.PARAM_VALID_ERROR.exception();
    }

    SplashAd dbSplashAd = splashAdMpService.checkExists(splashAdDTO.getEndPoint());
    if (Objects.isNull(dbSplashAd)) {
      //新增
      if (now.isAfter(splashAdDTO.getStartTime())) {
        //新增 也需要校验开始时间
        throw SystemCode.PARAM_VALID_ERROR.exception();
      }
      RedisUtil.doubleDeletion(() -> {
        splashAdMpService.save(splashAdDTO.toEntity());
      }, RedisUtil.delete(PlatformUtil.splashAdKey(splashAdDTO.getEndPoint())));


    } else {
      //修改
      RedisUtil.doubleDeletion(() -> {
        SplashAd entity = splashAdDTO.toEntity();
        entity.setId(dbSplashAd.getId());
        entity.setVersion(dbSplashAd.getVersion());
        splashAdMpService.updateById(entity);
      }, () -> {
        RedisUtil.delete(PlatformUtil.splashAdKey(splashAdDTO.getEndPoint()),
            PlatformUtil.splashAdUseInfoOnceKey(splashAdDTO.getEndPoint()),
            PlatformUtil.splashAdUseInfoDayMultiKey(splashAdDTO.getEndPoint()));
      });


    }

  }

  @Override
  public SplashAdDTO queryByEndPoint(DecorationEndpointTypeEnum endPoint) {
    SplashAdDTO splashAdDTO = RedisUtil.getCacheMap(SplashAdDTO.class,
        () -> splashAdMpService.queryAdByEndPoint(endPoint),
        Duration.ofDays(CommonPool.NUMBER_ONE),
        PlatformUtil.splashAdKey(endPoint));
    if (Objects.isNull(splashAdDTO) || Objects.isNull(splashAdDTO.getShowFlag())) {
      return null;
    }
    return splashAdDTO;
  }

  /**
   * 使用开屏广告
   *
   * @param endPoint
   * @return
   */
  @Override
  public SplashAdDTO useSplashAd(DecorationEndpointTypeEnum endPoint) {
    SplashAdDTO splashAdDTO = queryByEndPoint(endPoint);
    if (Objects.isNull(splashAdDTO) ||
        !splashAdDTO.getShowFlag() ||
        LocalDateTime.now().isBefore(splashAdDTO.getStartTime()) ||
        LocalDateTime.now().isAfter(splashAdDTO.getEndTime())) {
      //终端未配置广告    广告未启用  广告未生效 不在投放时间段内
      return null;
    }
    SecureUser<Object> user = ISecurity.userOpt().getOrNull();
    if (Objects.isNull(user)) {
      //用户未登录 直接返回广告内容
      return splashAdDTO;
    }
    return splashAdDTO.getShowFrequency().getCheckShowFun().apply(splashAdDTO, user.getId());

  }
}
