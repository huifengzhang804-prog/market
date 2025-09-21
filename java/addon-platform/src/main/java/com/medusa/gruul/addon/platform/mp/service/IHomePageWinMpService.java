package com.medusa.gruul.addon.platform.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.platform.mp.entity.HomePageWin;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;

/**
 * @author jipeng
 * @since 2024/6/25
 */

public interface IHomePageWinMpService extends IService<HomePageWin> {

  /**
   * 根据终端查询首页弹框
   * @param endPoint 终端
   * @return 首页弹框id
   */
  HomePageWin queryByEndPoint(DecorationEndpointTypeEnum endPoint);
}
