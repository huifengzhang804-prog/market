package com.medusa.gruul.overview.api.model;


import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.overview.api.enums.ActivityConfigType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 类描述
 *
 * @author jipeng
 * @since 2024/3/15
 */
@Data
@Accessors(chain = true)
public class ActivityConfigDTO implements BaseDTO {

  /**
   * 活动配置类型
   */
  private ActivityConfigType type;
  /**
   * 活动配置内容
   */

  private String content;


}
