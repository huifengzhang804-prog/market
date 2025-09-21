package com.medusa.gruul.addon.platform.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * WEb配置参数模块枚举
 * @author jipeng
 * @since 2024/6/21
 */
@Getter
@RequiredArgsConstructor
public enum WebParamModuleEnum {


  PUBLIC_SETTING("公共设置",2),
  PLATFORM_SETTING("平台设置",3),
  SHOP_SETTING("店铺设置",4),
  SUPPLIER_SETTING("供应商设置",5),
  OTHERS_SETTING("其他端设置",6)
  ;
  ;
  /**
   * 模块名称
   */
  private final String moduleName;
  /**
   * 模块值
   */
  @EnumValue
  private final Integer value;
}
