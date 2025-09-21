package com.medusa.gruul.overview.api.enums;

import com.medusa.gruul.global.i18n.I18N;
import com.medusa.gruul.global.model.exception.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @description:
 * @projectName:gruul-mall-overview
 * @see:com.medusa.gruul.overview.api.enums
 * @author:jipeng
 * @createTime:2024/1/15 18:18
 * @version:1.0
 */
@Getter
@RequiredArgsConstructor
public enum OverviewError implements Error {

  /**
   * 用户订单上传失败
   */
  STATEMENT_UPLOADER_ERROR(70001, "statement.uploader.error"),
  /**
   * 临时EXCEL创建发生错误
   */
  TEMP_EXCEL_GENERATE_ERROR(70002, "temp.excel.generate.error"),

  ;
  private final int code;

  private final String msgCode;

  @Override
  public int code() {
    return getCode();
  }

  @Override
  public String msg() {
    return I18N.msg(getMsgCode());
  }

}
