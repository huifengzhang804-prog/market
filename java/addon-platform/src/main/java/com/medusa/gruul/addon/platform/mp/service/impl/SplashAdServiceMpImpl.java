package com.medusa.gruul.addon.platform.mp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.platform.model.dto.SplashAdDTO;
import com.medusa.gruul.addon.platform.mp.entity.SplashAd;
import com.medusa.gruul.addon.platform.mp.mapper.SplashAdMapper;
import com.medusa.gruul.addon.platform.mp.service.ISplashAdMpService;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import java.util.Objects;
import java.util.function.Supplier;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**开屏广告
 * @author jipeng
 * @since 2024/6/24
 */
@Service
public class SplashAdServiceMpImpl extends ServiceImpl<SplashAdMapper, SplashAd> implements
    ISplashAdMpService {

  /**
   * 查询指定终端是是否存在开屏广告
   * @param endPoint 终端类型
   * @return 存在返回广告id，不存在返回null
   */
  @Override
  public SplashAd checkExists(DecorationEndpointTypeEnum endPoint) {
   return TenantShop.disable(()->{
      LambdaQueryWrapper<SplashAd> queryWrapper = new LambdaQueryWrapper<SplashAd>()
              .select(SplashAd::getId,SplashAd::getVersion)
              .eq(SplashAd::getEndPoint, endPoint);
      return getBaseMapper().selectOne(queryWrapper);
    });

  }

  @Override
  public SplashAdDTO queryAdByEndPoint(DecorationEndpointTypeEnum endPoint) {
    SplashAd splashAd = TenantShop.disable(() -> baseMapper.selectOne(
            new LambdaQueryWrapper<SplashAd>().eq(SplashAd::getEndPoint, endPoint)));

    if (Objects.isNull(splashAd)) {
      return new SplashAdDTO();
    }
    SplashAdDTO target = new SplashAdDTO();
    BeanUtils.copyProperties(splashAd, target);
    return target;
  }
}
