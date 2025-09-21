package com.medusa.gruul.addon.platform.controller;

import com.medusa.gruul.addon.platform.model.enums.WebParamKeyEnum;
import com.medusa.gruul.addon.platform.model.enums.WebParamModuleEnum;
import com.medusa.gruul.addon.platform.service.PlatformConfigService;
import com.medusa.gruul.common.idem.annotation.Idem;
import com.medusa.gruul.common.log.annotation.Log;
import com.medusa.gruul.common.model.resp.Result;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jipeng
 * @since 2024/6/21
 */
@RestController
@Validated
@RequiredArgsConstructor

@RequestMapping("/platform/config")
public class PlatformConfigController {

  private final PlatformConfigService platformConfigService;

  /**
   * 根据模块查询配置key信息
   * @param moduleList 模块列表
   * @return 配置信息
   */
  @PreAuthorize("@S.platformPerm('platformConfig')")
  @GetMapping("configInfo")
  @Log("根据模块查询配置key信息")
  public Result<List<Map<String, String>>> queryConfigInfoByModule(
      @RequestParam("modules") List<WebParamModuleEnum> moduleList) {


    return Result.ok(WebParamKeyEnum.queryConfigByModule(moduleList));
  }

  /**
   * 更新或者保存配置
   * @param configMap
   * @return
   */
  @PreAuthorize("@S.platformPerm('platformConfig')")
  @Idem
  @Log("更新或者保存配置")
  @PostMapping("saveOrUpdate")
  public Result<Boolean> saveOrUpdateConfig(@RequestBody Map<WebParamKeyEnum,String> configMap){

    platformConfigService.saveOrUpdateConfig(configMap);
    return Result.ok(true);
  }


  /**
   * 根据模块查询配置信息
   * @param modules 模块列表
   * @return 模块配置信息
   */
  @GetMapping("query-config-by-module")
  @Log("根据模块查询配置信息")
  @PreAuthorize("permitAll()")
  public Result<Map<String,String>> queryConfigParamByModule(@RequestParam("modules")
  List<WebParamModuleEnum> modules){
    Map<String,String> result=platformConfigService.queryConfigParamByModuleList(modules);
    return Result.ok(result);
  }

  @GetMapping("query-config-by-key")
  @Log("根据key查询配置信息")
  @PreAuthorize("permitAll()")
  public Result<Map<WebParamKeyEnum,String>> queryConfigParamByKey(@RequestParam("keys") List<WebParamKeyEnum> keys){
    Map<WebParamKeyEnum,String> result=platformConfigService.queryConfigByKeys(keys);
    return Result.ok(result);
  }





}
