package com.medusa.gruul.addon.platform.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 跳过方式
 * @author jipeng
 * @since 2024/6/24
 */
@Getter
@RequiredArgsConstructor
public enum AdSkipWayEnum {

  NO_SKIP(0, "不跳过"),
  MANUAL_SKIP_AFTER_SECONDS(1, "手工跳过，过多少秒"),
  AUTO_SKIP_AFTER_SECONDS(2, "自动跳过，过多少秒"),
  ;   // 自动跳过，过多少秒

  @EnumValue
  private final Integer value;
  private final String desc;

}
