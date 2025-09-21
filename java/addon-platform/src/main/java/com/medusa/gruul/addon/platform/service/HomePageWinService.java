package com.medusa.gruul.addon.platform.service;

import com.medusa.gruul.addon.platform.model.dto.HomePageWinDTO;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;

/**
 * @author jipeng
 * @since 2024/6/25
 */
public interface HomePageWinService {

  /**
   * 保存或更新
   * @param homePageWinDTO
   */
  void saveOrUpdate(HomePageWinDTO homePageWinDTO);

  /**
   * 根据终端类型获取弹框信息
   * @param endPoint
   * @return
   */
  HomePageWinDTO queryByEndPoint(DecorationEndpointTypeEnum endPoint);

  /**
   * 根据终端类型获取弹框信息
   * @param endPoint
   * @return
   */
  HomePageWinDTO useByEndPoint(DecorationEndpointTypeEnum endPoint);
}
