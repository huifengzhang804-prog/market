package com.medusa.gruul.addon.platform.service;

import com.medusa.gruul.addon.platform.model.dto.SplashAdDTO;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;

/** 开屏广告
 * @author jipeng
 * @since 2024/6/24
 */
public interface SplashAdService {

  /**
   * 保存或更新开屏广告
   * @param splashAdDTO 开屏广告DTO
   */
  void saveOrUpdate(SplashAdDTO splashAdDTO);

  /**
   * 根据终端类型查广告
   * @param endPoint
   * @return
   */
  SplashAdDTO queryByEndPoint(DecorationEndpointTypeEnum endPoint);

  /**
   * 使用开屏广告
   * @param endPoint
   * @return
   */
  SplashAdDTO useSplashAd(DecorationEndpointTypeEnum endPoint);
}
