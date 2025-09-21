package com.medusa.gruul.addon.team.model.dto;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 订单摘要查询条件
 *
 * @author 张治保
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class TeamSummaryDTO implements BaseDTO {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 团号
     */
    private String teamNo;

    /**
     * 订单号
     */
    private String orderNo;

    @Override
    public void validParam() {
        if (StrUtil.isEmpty(teamNo) && StrUtil.isEmpty(orderNo)) {
            throw new GlobalException("团号和订单号不能同时为空");
        }
    }
}
