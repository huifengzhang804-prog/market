package com.medusa.gruul.goods.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author jipeng
 * @since 2024/7/4
 */
@Getter
@AllArgsConstructor
public enum ProductSortEnum {

  SUBMIT_TIME_ASC("submit_time_asc", "提交时间升序"),
  SUBMIT_TIME_DESC("submit_time_desc", "提交时间降序"),
  AUDIT_TIME_ASC("audit_time_asc", "审核时间升序"),
  AUDIT_TIME_DESC("audit_time_desc", "审核时间降序"),
  //按照商品所有SKU的最低价格进行排序
  SALE_PRICE_ASC("sale_price_asc", "价格升序"),
  SALE_PRICE_DESC("sale_price_desc", "价格降序"),
  STOCK_ASC("stock_asc", "库存升序"),
  STOCK_DESC("stock_desc", "库存降序"),
  SALE_COUNT_ASC("sale_count_asc", "已售升序"),
  SALE_COUNT_DESC("sale_count_desc", "已售降序"),
  ;
  private final String code;
  private final String desc;
}
