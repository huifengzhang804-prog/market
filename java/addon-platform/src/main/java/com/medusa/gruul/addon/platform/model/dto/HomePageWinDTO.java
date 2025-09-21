package com.medusa.gruul.addon.platform.model.dto;

import com.medusa.gruul.addon.platform.mp.entity.HomePageWin;
import com.medusa.gruul.shop.api.enums.DecorationEndpointTypeEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 * 首页弹框DTO
 * @author jipeng
 * @since 2024/6/25
 */
@Data
public class HomePageWinDTO {

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





  @NotNull
  private AdImgDetailDTO imageInfo;


  /**
   * 终端类型
   */
  @NotNull
  private DecorationEndpointTypeEnum endPoint;

  /**
   * 后台未设置弹框 缓存标记位 防止缓存穿透
   */
  private Boolean notExists=Boolean.FALSE;

  public HomePageWin toEntity() {
    HomePageWin homePageWin = new HomePageWin();
    homePageWin.setShowFlag(this.getShowFlag());
    homePageWin.setImageInfo(this.getImageInfo());
    homePageWin.setStartTime(this.getStartTime());
    homePageWin.setEndTime(this.getEndTime());
    homePageWin.setEndPoint(this.getEndPoint());
    return homePageWin;

  }
}
