package com.medusa.gruul.addon.invoice.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 排序类型枚举
 * @author jipeng
 * @since 2024/6/28
 */
@Getter
@RequiredArgsConstructor
public enum SortEnum {

  APPLY_TIME_ASC(1, "申请时间升序"),
  APPLY_TIME_DESC(2, "申请时间降序"),
  UPDATE_TIME_ASC(3, "更新时间升序"),
  UPDATE_TIME_DESC(4, "更新时间降序");

  @EnumValue
  private final Integer value;
  private final String content;

}
