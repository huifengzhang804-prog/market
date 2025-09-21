package com.medusa.gruul.addon.full.reduction.model.dto;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 满减活动下架参数
 *
 * @author 张治保
 * @since 2024/6/17
 */
@Getter
@Setter
@ToString
public class FullReductionShelfDTO implements BaseDTO {

    /**
     * 店铺id
     */

    private Long shopId;

    /**
     * 活动id
     */
    @NotNull
    private Long id;


    /**
     * 下架原因
     */
    @Size(max = 50)
    private String violation;

    public void validParam(boolean isShop) {
        if (!isShop) {
            if (shopId == null) {
                throw SystemCode.PARAM_VALID_ERROR.exception("shopId cannot be null");
            }

            if (StrUtil.isEmpty(violation)) {
                throw SystemCode.PARAM_VALID_ERROR.exception("reason cannot be empty");
            }
        }
    }
}
