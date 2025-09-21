package com.medusa.gruul.storage.api.dto.activity;

import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 新增/编辑 活动时 预处理库存
 *
 * @author 张治保
 * date 2023/3/6
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ActivityCreateDTO implements BaseDTO {

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 订单类型
     */
    @NotNull
    private OrderType activityType;

    /**
     * 活动id
     */
    @NotNull
    private Long activityId;

    /**
     * sku列表
     */
    @NotNull
    @Size(min = 1)
    private List<ActivitySkuDTO> skus;

    /**
     * 活动开始时间
     */
    @NotNull
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    @NotNull
    private LocalDateTime endTime;

    @Override
    public void validParam() {
        if (activityType == OrderType.COMMON) {
            throw new IllegalArgumentException("not support common activity type");
        }
    }
}
