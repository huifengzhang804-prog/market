package com.medusa.gruul.user.api.enums;

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
public enum UserError implements Error {

  /**
   * 用户订单上传失败
   */
  EXCEL_UPLOADER_ERROR(80001, "excel.uploader.error"),
  /**
   * 临时EXCEL创建发生错误
   */
  TEMP_EXCEL_GENERATE_ERROR(80002, "temp.excel.generate.error"),

  USER_RIGHT_IN_USE_CAN_NOT_DELETE(80003, "user.right.in.use.can.not.delete"),

  USER_RIGHT_IS_OPEN_CAN_NOT_DELETE(80004, "user.right.is.open.can.not.delete"),

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
