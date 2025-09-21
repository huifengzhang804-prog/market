package com.medusa.gruul.addon.distribute.model.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 分销商审核VO
 * @author jipeng
 * @since 2024/8/23
 */
@Data
public class DistributorAuditVo implements Serializable {

  @Size(min = 1)
  @NotNull
  /**
   * 主键id
   */
  private List<Long> Ids;
  /**
   * 是否通过
   */
  @NotNull
  private Boolean pass;
  /**
   * 不通过的拒绝原因
   */
  private String rejectReason;
}
