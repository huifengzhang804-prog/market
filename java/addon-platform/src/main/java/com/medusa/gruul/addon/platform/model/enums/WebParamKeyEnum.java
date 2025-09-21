package com.medusa.gruul.addon.platform.model.enums;

import cn.hutool.core.collection.CollectionUtil;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.utils.Lists;

/**
 * 平台参数配置Key枚举
 *
 * @author jipeng
 * @since 2024/6/21
 */
@Getter
@RequiredArgsConstructor
public enum WebParamKeyEnum {

  //公共设置
  PLATFORM_NAME("platform_name", "平台名称", WebParamModuleEnum.PUBLIC_SETTING, String.class.getName()),
  COPYRIGHT_INFO("copyright_info", "版权信息", WebParamModuleEnum.PUBLIC_SETTING, String.class.getName()),
  COPYRIGHT_URL("copyright_url", "版权链接地址", WebParamModuleEnum.PUBLIC_SETTING, String.class.getName()),
  RECORDER_INFO("recorder_info", "备案信息", WebParamModuleEnum.PUBLIC_SETTING, String.class.getName()),
  RECORDER_URL("recorder_url", "备案链接地址", WebParamModuleEnum.PUBLIC_SETTING, String.class.getName()),
  WEB_SIT_ICON("web_sit_icon", "网站图标", WebParamModuleEnum.PUBLIC_SETTING, String.class.getName()),
  LOGIN_LOGO("login_logo", "登录页logo", WebParamModuleEnum.PUBLIC_SETTING, String.class.getName()),
  //平台端
  PLATFORM_WEB_SIT_NAME("platform_web_sit_name", "平台网站名称", WebParamModuleEnum.PLATFORM_SETTING, String.class.getName()),
  PLATFORM_LOGIN_PAGE_BG("platform_login_page_bg", "平台登录页背景图", WebParamModuleEnum.PLATFORM_SETTING, String.class.getName()),
  PLATFORM_LOGO("platform_logo", "平台logo", WebParamModuleEnum.PLATFORM_SETTING, String.class.getName()),
  //商家端
  SHOP_WEB_SIT_NAME("shop_web_sit_name", "商家网站名称", WebParamModuleEnum.SHOP_SETTING, String.class.getName()),
  SHOP_LOGIN_PAGE_BG("shop_login_page_bg", "商家登录页背景图", WebParamModuleEnum.SHOP_SETTING, String.class.getName()),

  //供应商端
  SUPPLIER_WEB_SIT_NAME("supplier_web_sit_name", "供应商网站名称", WebParamModuleEnum.SUPPLIER_SETTING, String.class.getName()),
  SUPPLIER_LOGIN_PAGE_BG("supplier_login_page_bg", "供应商登录页背景图", WebParamModuleEnum.SUPPLIER_SETTING, String.class.getName()),

  //其他端 网站名称
  H5_MALL_NAME("h5_mall_name", "H5商城名称", WebParamModuleEnum.OTHERS_SETTING, String.class.getName()),
  PC_MALL_NAME("pc_mall_name", "PC商城名称", WebParamModuleEnum.OTHERS_SETTING, String.class.getName()),
  SHOP_MOBILE_NAME("shop_mobile_name", "商家移动端名称", WebParamModuleEnum.OTHERS_SETTING, String.class.getName()),
  STORE_MOBILE_NAME("store_mobile_name", "门店移动端名称", WebParamModuleEnum.OTHERS_SETTING, String.class.getName()),
  ;


  /**
   * 参数key
   */
  @EnumValue
  private final String key;
  /**
   * 参数key
   */
  private final String desc;
  /**
   * 模块
   */
  private final WebParamModuleEnum module;
  /**
   * 数据类型
   */
  private final String dataType;

  /**
   * 根据模块获取配置参数
   * @param modules 模块列表
   * @return 参数列表
   */
  public static List<Map<String,String>> queryConfigByModule(List<WebParamModuleEnum> modules) {
    List<WebParamKeyEnum> configParamList = Arrays.stream(WebParamKeyEnum.values())
        .filter(webParamKeyEnum -> modules.contains(webParamKeyEnum.getModule())).collect(
            Collectors.toList());
    if (CollectionUtil.isEmpty(configParamList)) {
      return Lists.newArrayList();
    }
    return configParamList.stream().map(webParamKeyEnum -> {
      Map<String, String> map = Maps.newHashMap();
      map.put("key", webParamKeyEnum.name());
      map.put("desc", webParamKeyEnum.getDesc());
      map.put("module", webParamKeyEnum.getModule().name());
      map.put("dataType", webParamKeyEnum.getDataType());
      return map;
    }).collect(Collectors.toList());
  }


}
