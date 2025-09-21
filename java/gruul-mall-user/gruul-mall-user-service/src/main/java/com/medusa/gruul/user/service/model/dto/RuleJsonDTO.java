package com.medusa.gruul.user.service.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author xiaoq
 * @Description
 * @date 2022-09-01 15:06
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
@Data
public class RuleJsonDTO {

    private Long id;


    /**
     * 充值金额
     */
    private Long ladderMoney;

    /**
     * 赠送金额
     */
    private Long presentedMoney;

    /**
     * 赠送成长值
     */
    private Long presentedGrowthValue;
}


