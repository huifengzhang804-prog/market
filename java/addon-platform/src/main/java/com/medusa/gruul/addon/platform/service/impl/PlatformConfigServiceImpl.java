package com.medusa.gruul.addon.platform.service.impl;

import cn.hutool.core.collection.CollectionUtil;

import com.google.common.collect.Maps;
import com.medusa.gruul.addon.platform.model.enums.WebParamKeyEnum;
import com.medusa.gruul.addon.platform.model.enums.WebParamModuleEnum;
import com.medusa.gruul.addon.platform.mp.entity.WebParamConfig;
import com.medusa.gruul.addon.platform.mp.service.IWebParamConfigService;
import com.medusa.gruul.addon.platform.service.PlatformConfigService;
import com.medusa.gruul.addon.platform.util.PlatformUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.redis.util.RedisUtil;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author jipeng
 * @since 2024/6/21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformConfigServiceImpl implements PlatformConfigService {

  private final IWebParamConfigService webParamConfigService;
  public static final String EMPTY_KEY="EMPTY_KEY";


  /**
   * 保存或者更新配置
   * @param configMap 配置Map
   */
  @Override
  public void saveOrUpdateConfig(Map<WebParamKeyEnum, String> configMap) {
    Set<WebParamModuleEnum> moduleEnums = configMap.keySet().stream()
        .map(WebParamKeyEnum::getModule)
        .collect(Collectors.toSet());
    //根据模块查询已经存在的key
    Map<WebParamKeyEnum, WebParamConfig> existConfigMap = webParamConfigService.lambdaQuery()
        .in(WebParamConfig::getModule, moduleEnums)
        .list()
        .stream().collect(Collectors.toMap(WebParamConfig::getParamKey, x -> x));
    //新增
    List<WebParamConfig> saveList = Lists.newArrayList();
    //修改
    List<WebParamConfig> updateList = Lists.newArrayList();
    configMap.forEach((key, value) -> {
      WebParamConfig webParamConfig = new WebParamConfig();
      webParamConfig.setParamKey(key);
      webParamConfig.setParamValue(value);
      webParamConfig.setModule(key.getModule());
      WebParamConfig dbParamConfig = existConfigMap.get(key);
      if (Objects.isNull(dbParamConfig)) {
        saveList.add(webParamConfig);
      } else {
        webParamConfig.setId(dbParamConfig.getId());

        updateList.add(webParamConfig);
      }
    });
    if (CollectionUtil.isNotEmpty(saveList)) {
      RedisUtil.doubleDeletion(()->{
        webParamConfigService.saveBatch(saveList);
      },()->{
        //删除缓存
        Set<String> keySet = configMap.keySet().stream()
            .map(x -> PlatformUtil.paramConfigModuleKey(x.getModule())).collect(Collectors.toSet());
        RedisUtil.delete(keySet);
      });

    }
    if (CollectionUtil.isNotEmpty(updateList)) {
     RedisUtil.doubleDeletion(()->{
       webParamConfigService.updateBatchById(updateList);
     },()->{
       //删除缓存
       Set<String> keySet = configMap.keySet().stream()
           .map(x -> PlatformUtil.paramConfigModuleKey(x.getModule())).collect(Collectors.toSet());
       RedisUtil.delete(keySet);
     });
    }


  }



  @Override
  public Map<String, String> queryConfigParamByModuleList(List<WebParamModuleEnum> modules) {
    Map<String, String> result = Maps.newHashMap();
    modules.forEach(x -> {
      Map<String, String> map = queryParamByModule(x);
      if (CollectionUtil.isNotEmpty(map)) {
        result.putAll(map);
      }
    });
    return result;
  }

  @Override
  public Map<WebParamKeyEnum, String> queryConfigByKeys(List<WebParamKeyEnum> keys) {
    //对keys进行分组
    Map<WebParamModuleEnum, List<WebParamKeyEnum>> groupMap = keys.stream()
        .collect(Collectors.groupingBy(WebParamKeyEnum::getModule));
    Map<WebParamKeyEnum, String> result = Maps.newHashMap();
    groupMap.forEach((key, value) -> {
      Map<String, String> map = queryParamByModule(key);
      if (CollectionUtil.isNotEmpty(map)) {
        for (WebParamKeyEnum webParamKeyEnum : value) {
          result.put(webParamKeyEnum, map.get(webParamKeyEnum.name()));
        }
      }
    });
    return result;
  }

  /**
   * 根据模块获取配置Map
   * @param webParamModuleEnum
   * @return
   */
  private Map<String,String> queryParamByModule(WebParamModuleEnum webParamModuleEnum) {
    Map<String,String> cacheMap = RedisUtil.getCacheMap(Map.class, () -> {
          List<WebParamConfig> list = webParamConfigService.lambdaQuery()
              .select(WebParamConfig::getParamValue,WebParamConfig::getParamKey)
              .eq(WebParamConfig::getModule, webParamModuleEnum)
              .list();
          if (CollectionUtil.isEmpty(list)) {
            HashMap<String, String> emptyMap = new HashMap<>();
            emptyMap.put(EMPTY_KEY,StringUtils.EMPTY);
            return emptyMap;
          }
          Map<String, String> map = Maps.newHashMap();
          for (WebParamConfig webParamConfig : list) {
            map.put(webParamConfig.getParamKey().name(), webParamConfig.getParamValue());
          }
          return map;
        }
        , Duration.ofDays(CommonPool.NUMBER_ONE),
        PlatformUtil.paramConfigModuleKey(webParamModuleEnum));
    if (CollectionUtil.isEmpty(cacheMap)||cacheMap.containsKey(EMPTY_KEY)) {
      return Maps.newHashMap();
    }

    return cacheMap;
  }
}
