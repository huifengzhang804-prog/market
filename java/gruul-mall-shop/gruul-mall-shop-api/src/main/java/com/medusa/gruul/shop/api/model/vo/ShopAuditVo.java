package com.medusa.gruul.shop.api.model.vo;

import lombok.Data;

/**
 * @author jipeng
 * @since 2024/8/19
 */
@Data
public class ShopAuditVo {

  /**
   * 店铺id
   */
  private Long shopId;
  /**
   * 是否通过审核
   */
  private Boolean passFlag;
  /**
   * 拒绝原因
   */
  private String reasonForRejection;

}
