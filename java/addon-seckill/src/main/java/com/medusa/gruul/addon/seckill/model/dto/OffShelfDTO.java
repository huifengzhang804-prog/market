package com.medusa.gruul.addon.seckill.model.dto;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 秒杀活动下架
 *
 * @author 张治保
 * @since 2024/6/4
 */
@Getter
@Setter
@ToString
public class OffShelfDTO implements Serializable {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 活动id
     */
    @NotNull
    private Long activityId;

    /**
     * 下架原因
     */
    @Size(max = 50)
    private String reason;

    /**
     * 参数校验
     *
     * @param isShop 是否是店铺自行下架
     */
    public void validParam(boolean isShop) {
        if (!isShop && shopId == null) {
            throw SystemCode.PARAM_VALID_ERROR.dataEx("shopId is required");
        }
        //平台下架 下架原因必填
        if (shopId != null && StrUtil.isEmpty(reason)) {
            throw SystemCode.PARAM_VALID_ERROR.dataEx("reason is required");
        }
    }
}
