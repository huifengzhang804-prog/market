package com.medusa.gruul.addon.platform.controller;

import com.medusa.gruul.addon.platform.model.dto.SplashAdDTO;
import com.medusa.gruul.addon.platform.service.SplashAdService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jipeng
 * @since 2024/6/24
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("splash")
public class SplashAdController {

  private final SplashAdService splashAdService;
  /**
   * 保存或者更新广告设置
   * @return 保存结果
   */
  @Log("保存或者更新广告设置")
  @Idem(value = 1000)
  @PostMapping("saveOrUpdate")
  @PreAuthorize("@S.platformPerm('splash')")
  public Result<Boolean> saveOrUpdate(@RequestBody @Valid SplashAdDTO splashAdDTO) {
    splashAdService.saveOrUpdate(splashAdDTO);
    return Result.ok(Boolean.TRUE);
  }

  /**
   * 查询广告设置
   * @param endPoint 终端类型
   * @return 广告数据
   */
  @Log("查询广告设置")
  @GetMapping("query/{endPoint}")
  @PreAuthorize("@S.platformPerm('splash')")
  public Result<SplashAdDTO> query(@PathVariable("endPoint") DecorationEndpointTypeEnum endPoint) {
    return Result.ok(splashAdService.queryByEndPoint(endPoint));
  }
  /**
   * 广告使用
   * @param endPoint 终端类型
   * @return 广告数据
   */
  @Log("广告使用")
  @GetMapping("use/{endPoint}")
  public Result<SplashAdDTO> use(@PathVariable("endPoint") DecorationEndpointTypeEnum endPoint) {
    return Result.ok(splashAdService.useSplashAd(endPoint));
  }



}
