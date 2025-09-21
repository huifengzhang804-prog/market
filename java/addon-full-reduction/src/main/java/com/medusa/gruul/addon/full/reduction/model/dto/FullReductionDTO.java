package com.medusa.gruul.addon.full.reduction.model.dto;


import com.medusa.gruul.addon.full.reduction.model.bo.FullReductionRule;
import com.medusa.gruul.addon.full.reduction.model.enums.ProductType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.global.model.o.RangeDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 新增、编辑满减活动参数
 */
@Getter
@Setter
@Accessors(chain = true)
public class FullReductionDTO implements BaseDTO {
    /**
     * 结束时间不能在五分钟之内
     */
    private static final int END_MINUTES_LIMIT = 5;

    /**
     * 满减id ,id为空时新增,不为空时编辑
     */
    private Long id;

    /**
     * 满减活动名称
     */
    @NotBlank
    @Size(max = 50)
    private String name;
    /**
     * 活动时间范围
     */
    @Valid
    private RangeDateTime time;

    /**
     * 满减规则
     */
    @NotNull
    @Valid
    @Size(min = 1)
    private List<FullReductionRule> rules;

    /**
     * 0:全部商品 1:指定商品 2:指定商品不参与
     */
    @NotNull
    private ProductType productType;

    /**
     * 商品ids
     */
    private Set<Long> productIds;

    @Override
    public void validParam() {
        if (productType != ProductType.ALL_PRODUCT && CollectionUtils.isEmpty(productIds)) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
        LocalDateTime now = LocalDateTime.now();
        if (getTime().getEnd().isBefore(now.plusMinutes(END_MINUTES_LIMIT))) {
            throw SystemCode.PARAM_VALID_ERROR.exception("end time must be greater than now + " + END_MINUTES_LIMIT + " minutes");
        }
    }

}
