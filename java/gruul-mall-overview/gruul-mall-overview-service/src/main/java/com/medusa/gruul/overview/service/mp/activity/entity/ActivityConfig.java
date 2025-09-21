package com.medusa.gruul.overview.service.mp.activity.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.overview.api.enums.ActivityConfigType;
import com.medusa.gruul.overview.api.model.ActivityConfigDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 活动配置
 *
 * @author jipeng
 * @since 2024/3/15
 */
@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName("t_activity_config")
public class ActivityConfig extends BaseEntity {

  /**
   * 活动配置类型
   */
  @TableField(value = "type")
  private ActivityConfigType type;
  /**
   * 活动配置内容
   */
  @TableField(value = "content")
  private String content;

  public static ActivityConfig build(ActivityConfigDTO dto){
    ActivityConfig activityConfig = new ActivityConfig();
    activityConfig.setType(dto.getType());
    activityConfig.setContent(dto.getContent());
    return activityConfig;
  }
}
