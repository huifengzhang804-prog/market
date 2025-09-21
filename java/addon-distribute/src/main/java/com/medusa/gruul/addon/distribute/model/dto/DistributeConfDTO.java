package com.medusa.gruul.addon.distribute.model.dto;

import com.medusa.gruul.addon.distribute.model.DistributorCondition;
import com.medusa.gruul.addon.distribute.model.enums.Level;
import com.medusa.gruul.addon.distribute.model.enums.Precompute;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author 张治保
 * date 2022/11/16
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class DistributeConfDTO extends BonusConfigDTO implements BaseDTO {

    /**
     * 分销层级
     */
    @NotNull
    private Level level;

    /**
     * 成为分销商的条件
     */
    @NotNull
    @Valid
    private DistributorCondition condition;

    /**
     * 预计算展示方式
     */
    private Precompute precompute;

    /**
     * 分销协议
     */
    @NotBlank
    private String protocol;

    /**
     * 攻略玩法
     */
    @NotBlank
    private String playMethods;

    /**
     * 是否开启内购
     */
    @NotNull
    private Boolean purchase;


    @Override
    public void validParam() {
        getCondition().validParam();
        super.validParam(getLevel());
    }


}
