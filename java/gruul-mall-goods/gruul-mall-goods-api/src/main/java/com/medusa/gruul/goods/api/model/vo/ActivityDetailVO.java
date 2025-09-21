package com.medusa.gruul.goods.api.model.vo;

import cn.hutool.json.JSON;
import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.global.model.o.RangeDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2024/6/24
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ActivityDetailVO implements Serializable {


  /**
   * 活动类型
   */
  private OrderType type;

  /**
   * 活动id
   */
  private Long activityId;

  /**
   * 活动价 当前活动进行中且sku参加活动时有值
   */
  private Long activityPrice;

  /**
   * 活动生效范围
   */
  private RangeDateTime time;

  /**
   * 活动叠加优惠 与支付超时时间
   */
  private StackableDiscount stackable;

  /**
   * 其它数据 如 每个sku对应价格、等
   */
  private JSON data;


}
