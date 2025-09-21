package com.medusa.gruul.addon.platform.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 展示图片详情
 * @author jipeng
 * @since 2024/8/27
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AdImgDetailDTO implements Serializable {

  /**
   * 图片地址
   */
  @NotNull
  private String url;
  /**
   * 展示时长
   */
  @NotNull
  @NotNull
  @Max(value = 60,message = "展示时长不能超过60秒")
  @Min(value = 1,message = "展示时长不能小于1秒")
  private Integer showTime;
  /**
   * 链接类型
   */
  @NotEmpty
  private String link;
}
