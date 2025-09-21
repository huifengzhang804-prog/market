package com.medusa.gruul.addon.platform.model.dto;

import com.medusa.gruul.addon.platform.model.enums.AdShowFrequencyEnum;
import com.medusa.gruul.addon.platform.model.enums.AdSkipWayEnum;
import com.medusa.gruul.addon.platform.mp.entity.SplashAd;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**开屏广告
 * @author jipeng
 * @since 2024/6/24
 */
@Data
public class SplashAdDTO {

  /**
   * 是否展示
   */
  @NotNull
  private Boolean showFlag;
  /**
   * 开始时间
   */
  @NotNull
  private LocalDateTime startTime;
  /**
   * 结束时间
   */
  @NotNull
  private LocalDateTime  endTime;
  /**
   * 跳过方式
   */
  @NotNull
  private AdSkipWayEnum skipWay;
  /**
   * 跳过时间 多少秒跳过
   */

  private Integer skipSecond= CommonPool.NUMBER_ZERO;
  /**
   * 展示频率
   */
  @NotNull
  private AdShowFrequencyEnum showFrequency;
  /**
   * 每日前几次展示的次数
   */

  private Integer times=CommonPool.NUMBER_ZERO;
  @Size(min = 1)
  private List<AdImgDetailDTO> imageList;
  /**
   * 终端类型
   */
  @NotNull
  private DecorationEndpointTypeEnum endPoint;

  /***
   * DTO转model
   * @return
   */
  public SplashAd toEntity() {
     SplashAd splashAd = new SplashAd();
     splashAd.setShowFlag(showFlag)
         .setStartTime(startTime)
         .setEndTime(endTime)
         .setSkipWay(skipWay)
         .setSkipSecond(skipSecond)
         .setShowFrequency(showFrequency)
         .setTimes(times)
         .setImageList(imageList)
         .setEndPoint(endPoint)
         .setDeleted(Boolean.FALSE)
         .setVersion(CommonPool.NUMBER_ZERO)
         .setCreateTime(LocalDateTime.now())
         .setUpdateTime(LocalDateTime.now());

     return splashAd;

  }


}
