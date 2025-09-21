package com.medusa.gruul.addon.platform.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.platform.model.dto.SplashAdDTO;
import com.medusa.gruul.addon.platform.mp.entity.SplashAd;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;

/** 开屏广告
 * @author jipeng
 * @since 2024/6/24
 */
public interface ISplashAdMpService extends IService<SplashAd> {

  /**
   * 判断指定的终端广告是否存在
   * @param endPoint
   * @return 广告
   */
  SplashAd checkExists(DecorationEndpointTypeEnum endPoint);

  /**
   * 根据终端类型查询广告
   * @param endPoint
   * @return
   */
  SplashAdDTO queryAdByEndPoint(DecorationEndpointTypeEnum endPoint);
}
