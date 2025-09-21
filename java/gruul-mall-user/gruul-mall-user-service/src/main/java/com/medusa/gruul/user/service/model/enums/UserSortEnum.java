package com.medusa.gruul.user.service.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author jipeng
 * @since 2024/7/1
 */
@Getter
@RequiredArgsConstructor
public enum UserSortEnum {

  BALANCE_ASC(1, "余额升序"),
  BALANCE_DESC(2, "余额降序"),
  REGISTER_TIME_ASC(3, "注册时间升序"),
  REGISTER_TIME_DESC(4, "注册时间降序"),
  RECENT_CONSUME_TIME_ASC(5, "最近消费时间升序"),
  RECENT_CONSUME_TIME_DESC(6, "最近消费时间降序"),
  ;
  private final Integer value;
  private final String content;
}
