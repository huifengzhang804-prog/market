package com.medusa.gruul.addon.platform.service;

import com.medusa.gruul.addon.platform.model.enums.WebParamKeyEnum;
import com.medusa.gruul.addon.platform.model.enums.WebParamModuleEnum;
import java.util.List;
import java.util.Map;

/**
 * @author jipeng
 * @since 2024/6/21
 */
public interface PlatformConfigService {

  /**
   * 保存更新web参数配置
   * @param configMap
   */
  void saveOrUpdateConfig(Map<WebParamKeyEnum, String> configMap);



  /**
   * 查询配置信息
   * @param modules 模块列表
   * @return 参数
   */
  Map<String, String> queryConfigParamByModuleList(List<WebParamModuleEnum> modules);

  /**
   * 根据key查询对应的配置信息
   * @param keys
   * @return
   */
  Map<WebParamKeyEnum, String> queryConfigByKeys(List<WebParamKeyEnum> keys);
}
