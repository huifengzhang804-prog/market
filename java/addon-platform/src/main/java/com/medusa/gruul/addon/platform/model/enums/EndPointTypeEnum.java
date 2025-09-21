package com.medusa.gruul.addon.platform.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 终端类型
 * @author jipeng
 * @since 2024/6/24
 */
@Getter
@RequiredArgsConstructor
public enum EndPointTypeEnum {
  /**
   * 微信小程序
   */
  WECHAT_MINI_APP(1),
  /**
   * H5,APP端
   */
  H5_APP(2),
  /**
   * PC商城
   */
  PC_MALL(3);
  @EnumValue
  private final Integer value;
}
