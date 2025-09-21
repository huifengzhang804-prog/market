package com.medusa.gruul.addon.platform.controller;

import com.medusa.gruul.addon.platform.model.dto.HomePageWinDTO;
import com.medusa.gruul.addon.platform.service.HomePageWinService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页弹框
 * @author jipeng
 * @since 2024/6/25
 */
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/home/page/win")
public class HomePageWinController {

  private final HomePageWinService homePageService;

  /**
   *  保存或者更新首页弹框
   * @param homePageWinDTO 首页弹框参数
   * @return 是否保存成功
   */
  @Log("保存或者更新首页弹框")
  @PostMapping("/saveOrUpdate")
  @Idem(value = 1000)
  @PreAuthorize("@S.platformPerm('homepageWin')")
  public Result<Boolean> saveOrUpdate(@RequestBody @Validated HomePageWinDTO homePageWinDTO) {
    homePageService.saveOrUpdate(homePageWinDTO);
    return Result.ok(true);
  }

  /**
   *  根据终端查询弹框
   * @param endPoint 终端类型
   * @return 弹框数据
   */
  @Log("根据终端查询弹框")
  @GetMapping("/queryByEndPoint/{endpoint}")
  @Idem(value = 1000)
  @PreAuthorize("@S.platformPerm('homepageWin')")
  public Result<HomePageWinDTO> queryByEndPoint(@PathVariable("endpoint") DecorationEndpointTypeEnum endPoint) {
    return Result.ok(homePageService.queryByEndPoint(endPoint));
  }

  /**
   *  根据终端获取广告
   * @param endPoint 终端类型
   * @return 广告数据
   */
  @Log("根据终端获取广告")
  @PreAuthorize("permitAll()")
  @GetMapping("/use/{endpoint}")
  public Result<HomePageWinDTO> useByEndPoint(@PathVariable("endpoint") DecorationEndpointTypeEnum endPoint) {
    return Result.ok(homePageService.useByEndPoint(endPoint));
  }

}
