package com.medusa.gruul.addon.platform.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.platform.model.enums.WebParamKeyEnum;
import com.medusa.gruul.addon.platform.model.enums.WebParamModuleEnum;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 平台参数配置
 *
 * @author jipeng
 * @since 2024/6/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_web_param_config")
public class WebParamConfig extends BaseEntity {

  /**
   * 配置Key
   */
  @TableField(value = "`param_key`")
  private WebParamKeyEnum paramKey;

  /**
   * 配置值
   */
  @TableField(value = "`param_value`")
  private String paramValue;

  /**
   * 配置所属模块
   */
  @TableField(value = "module")
  private WebParamModuleEnum module;





}
