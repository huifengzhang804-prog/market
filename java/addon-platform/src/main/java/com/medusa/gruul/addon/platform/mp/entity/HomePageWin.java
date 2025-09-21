package com.medusa.gruul.addon.platform.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.platform.model.dto.AdImgDetailDTO;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 首页弹框
 * @author jipeng
 * @since 2024/6/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_home_page_win",autoResultMap = true)
public class HomePageWin extends BaseEntity {

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
   * 广告图片json
   */
  @TableField(value = "image_info", typeHandler = Fastjson2TypeHandler.class)
  private AdImgDetailDTO imageInfo;
  /**
   * 终端类型
   */
  @TableField(value = "end_point")
  private DecorationEndpointTypeEnum endPoint;

}
