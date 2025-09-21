package com.medusa.gruul.overview.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @description: 数据导出状态
 * @projectName:gruul-mall-overview
 * @see:com.medusa.gruul.overview.api.enums
 * @author:jipeng
 * @createTime:2024/1/5 11:15
 * @version:1.0
 */
@Getter
@RequiredArgsConstructor
public enum DataExportStatus {


  /**
   * 进行中
   */
  PROCESSING(0, "生成中"),
  /**
   * 已完成
   */
  SUCCESS(1, "已完成"),
  /**
   * 失败
   */
  FAILED(2, "失败"),

  ;
  @EnumValue
  private final Integer value;

  private final String name;
}
