package com.medusa.gruul.overview.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 导出订单类型
 */
@Getter
@RequiredArgsConstructor
public enum ExportDataType {

  /**
   * 用户订单
   */
  USER_ORDER(1, "用户订单"),
  /**
   * 采购订单
   */
  PURCHASE_ORDER(2, "采购订单"),
  /**
   * 售后工单
   */
  AFTER_SALES_WORK_ORDER(3, "售后工单"),
  /**
   * 对账单
   */
  STATEMENT_OF_ACCOUNT(4,"对账单"),
  /**
   * 储值流水
   */
  STORED_VALUE_FLOW(5,"储值流水"),
  /**
   * 会员记录
   */
  MEMBER_RECORDER(6,"开通会员"),
  /**
   * 店铺结算
   */
  STORE_SETTLEMENT(7,"店铺结算"),
  /**
   * 供应商结算
   */
  SUPPLIER_SETTLEMENT(8,"供应商结算"),
  /**
   * 工单提现
   */

  WITHDRAWAL_ORDER(9,"提现工单"),

  /**
   * 消费返利订单
   */
  PURCHASE_REBATE_ORDER(10,"消费返利订单"),
  /**
   * 分销订单
   */
  DISTRIBUTE_ORDER(11,"分销订单"),

  /**
   * 库存明细
   */
  STORAGE_DETAIL(13,"库存明细"),

  COUPON_RECORD(14,"用券记录"),


  ;

  @EnumValue
  private final Integer value;

  private final String name;

}
