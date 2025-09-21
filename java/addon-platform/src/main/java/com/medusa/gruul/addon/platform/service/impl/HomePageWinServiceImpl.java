package com.medusa.gruul.addon.platform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medusa.gruul.addon.platform.model.dto.HomePageWinDTO;
import com.medusa.gruul.addon.platform.mp.entity.HomePageWin;
import com.medusa.gruul.addon.platform.mp.service.IHomePageWinMpService;
import com.medusa.gruul.addon.platform.service.HomePageWinService;
import com.medusa.gruul.addon.platform.util.PlatformUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author jipeng
 * @since 2024/6/25
 */
@Service
@RequiredArgsConstructor
public class HomePageWinServiceImpl implements HomePageWinService {

  private final IHomePageWinMpService homePageWinMpService;


  @Override
  public void saveOrUpdate(HomePageWinDTO homePageWinDTO) {
    LocalDateTime now = LocalDateTime.now();

    //参数校验是在开启弹框显示的时候才进行校验弹框结束时间需要再当前时间之后
    if (homePageWinDTO.getShowFlag()&&now.isAfter(homePageWinDTO.getEndTime())) {
      throw SystemCode.PARAM_VALID_ERROR.exception();
    }
    HomePageWin homePageWin = homePageWinMpService.queryByEndPoint(homePageWinDTO.getEndPoint());
    //如果是新增 还需要校验开始时间在当前时间之后
    if (Objects.isNull(homePageWin)) {
      if (homePageWinDTO.getShowFlag()&&now.isAfter(homePageWinDTO.getStartTime())) {
        throw SystemCode.PARAM_VALID_ERROR.exception();
      }
      //保存
      homePageWinMpService.saveOrUpdate(homePageWinDTO.toEntity());
    } else {
      //更新
      HomePageWin entity = homePageWinDTO.toEntity();
      entity.setVersion(homePageWin.getVersion())
          .setId(homePageWin.getId());
      homePageWinMpService.updateById(entity);
    }
    //删除缓存
    String key = PlatformUtil.homePageWinCacheKey(homePageWinDTO.getEndPoint());
    RedisUtil.delete(key);

  }

  @Override
  public HomePageWinDTO queryByEndPoint(DecorationEndpointTypeEnum endPoint) {

    HomePageWinDTO dto = RedisUtil.getCache(() -> {
          HomePageWinDTO homePageWinDTO = RedisUtil.getCacheMap(
              PlatformUtil.homePageWinCacheKey(endPoint), HomePageWinDTO.class);
          return homePageWinDTO;
        }, () -> {
          HomePageWin homePageWin = homePageWinMpService.getOne(new LambdaQueryWrapper<HomePageWin>()
              .eq(HomePageWin::getEndPoint, endPoint));
          if (Objects.isNull(homePageWin)) {
            //缓存不存在 设置标记位
            HomePageWinDTO homePageWinDTO = new HomePageWinDTO();
            homePageWinDTO.setNotExists(Boolean.TRUE);
            return homePageWinDTO;
          }
          return BeanUtil.toBean(homePageWin, HomePageWinDTO.class);
        }, Duration.ofDays(CommonPool.NUMBER_ONE),
        PlatformUtil.homePageWinCacheKey(endPoint));
    if (Objects.isNull(dto) || dto.getNotExists()) {
      return null;
    }
    return dto;
  }

  @Override
  public HomePageWinDTO useByEndPoint(DecorationEndpointTypeEnum endPoint) {
    HomePageWinDTO homePageWinDTO = queryByEndPoint(endPoint);
    if (Objects.isNull(homePageWinDTO)) {
      return null;
    }
    if (homePageWinDTO.getShowFlag()&&
        LocalDateTime.now().isAfter(homePageWinDTO.getStartTime())&&
        LocalDateTime.now().isBefore(homePageWinDTO.getEndTime())) {
      return homePageWinDTO;
    }
    return null;
  }
}
