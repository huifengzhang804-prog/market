package com.medusa.gruul.addon.platform.mp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.platform.mp.entity.HomePageWin;
import com.medusa.gruul.addon.platform.mp.mapper.HomePageWinMapper;
import com.medusa.gruul.addon.platform.mp.service.IHomePageWinMpService;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author jipeng
 * @since 2024/6/25
 */
@Service
@Slf4j
public class HomePageWinMpServiceImpl extends ServiceImpl<HomePageWinMapper, HomePageWin> implements
    IHomePageWinMpService {

  /**
   * 根据终端查询首页弹框
   * @param endPoint 终端
   * @return
   */
  @Override
  public HomePageWin queryByEndPoint(DecorationEndpointTypeEnum endPoint) {
    LambdaQueryWrapper<HomePageWin> queryWrapper = new LambdaQueryWrapper<HomePageWin>()
        .select(HomePageWin::getId,HomePageWin::getVersion)
        .eq(HomePageWin::getEndPoint, endPoint);
    return baseMapper.selectOne(queryWrapper);

  }
}
