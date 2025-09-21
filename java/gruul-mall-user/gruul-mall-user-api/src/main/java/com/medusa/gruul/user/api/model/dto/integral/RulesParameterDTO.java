package com.medusa.gruul.user.api.model.dto.integral;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.TreeMap;

/**
 * 积分规则DTO
 *
 * @author xiaoq
 * @Description 积分规则dto
 * @date 2023-02-03 15:53
 */
@Getter
@Setter
@ToString
public class RulesParameterDTO {

    /**
     * 基础值
     */
    private Integer basicsValue;

    /**
     * HashMap<连续登入天数,对应积分值>
     */
    private TreeMap<Integer, Integer> extendValue;

}
