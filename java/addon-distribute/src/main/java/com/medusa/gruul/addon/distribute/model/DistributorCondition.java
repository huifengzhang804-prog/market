package com.medusa.gruul.addon.distribute.model;

import com.medusa.gruul.addon.distribute.model.enums.ConditionType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.web.valid.annotation.Price;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * 成为分销员的条件
 *
 * @author 张治保
 * date 2022/11/15
 */
@Getter
@Setter
@Accessors(chain = true)
public class DistributorCondition implements BaseDTO {

    /**
     * 成为分销商的条件
     */
    @NotNull
    @Size(min = 1, max = 2)
    private Set<ConditionType> types;

    /**
     * 含 满足消费金额 时需要的金额
     */
    @Price
    private Long requiredAmount;

    @Override
    public void validParam() {
        if (types.contains(ConditionType.CONSUMPTION) && requiredAmount == null) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
        }
    }
}
