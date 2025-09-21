package com.medusa.gruul.overview.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动配置类型
 *
 * @author jipeng
 * @since 2024/3/15
 */
@Getter
@AllArgsConstructor
public enum ActivityConfigType {

  SEC_KILL_ACTIVITY_DURATION(1, "秒杀活动时长","addon:seckill:activity:duration"),
  SEC_KILL_ACTIVITY_RULE(2,"秒杀活动规则","addon:seckill:activity:rule"),

  BARGAIN_ACTIVITY_RULE(3,"砍价活动规则","addon:bargain:activity:rule"),

  TEAM_ACTIVITY_RULE(4,"拼团活动规则","addon:team:activity:rule"),

  ;
  @EnumValue
  private Integer value;
  private String desc;
  private String key;


}
