package com.medusa.gruul.addon.platform.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.platform.model.dto.AdImgDetailDTO;
import com.medusa.gruul.addon.platform.model.enums.AdShowFrequencyEnum;
import com.medusa.gruul.addon.platform.model.enums.AdSkipWayEnum;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**开屏广告
 * @author jipeng
 * @since 2024/6/24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_splash_ad",autoResultMap = true)
public class SplashAd  extends BaseEntity {

  /**
   * 是否展示
   */
  @TableField(value = "show_flag")
  private Boolean showFlag;
  /**
   * 开始时间
   */
  @TableField(value = "start_time")
  private LocalDateTime startTime;
  /**
   * 结束时间
   */
  @TableField(value = "end_time")
  private LocalDateTime  endTime;
  /**
   * 跳过方式
   */
  @TableField(value = "skip_way")
  private AdSkipWayEnum skipWay;
  /**
   * 跳过时间 多少秒跳过
   */
  @TableField(value = "skip_second")
  private Integer skipSecond;
  /**
   * 展示频率
   */
  @TableField(value = "show_frequency")
  private AdShowFrequencyEnum showFrequency;
  /**
   * 每日前几次展示的次数
   */
  @TableField(value = "times")
  private Integer times;
  /**
   * 广告图片json
   */
  @TableField(typeHandler = Fastjson2TypeHandler.class)
  private List<AdImgDetailDTO> imageList;

  /**
   * 终端类型
   */
  @TableField(value = "end_point")
  private DecorationEndpointTypeEnum endPoint;




}
