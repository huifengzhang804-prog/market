package com.medusa.gruul.addon.matching.treasure.model.dto;

import lombok.Data;

/**
 * @author jipeng
 * @since 2024/7/2
 */
@Data
public class SetMealProductStats {

  /**
   * 套餐Id
   */
  private Long setMealId;
  /**
   * 产品数量
   */
  private Integer productCount;
  /**
   * 订单数量
   */
  private Integer orderCount;
}
