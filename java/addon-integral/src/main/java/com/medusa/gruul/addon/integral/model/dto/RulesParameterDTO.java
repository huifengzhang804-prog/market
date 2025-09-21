package com.medusa.gruul.addon.integral.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

/**
 * 积分规则DTO
 *
 * @author xiaoq
 * @Description 积分规则dto
 * @date 2023-02-03 15:53
 */
@Data
public class RulesParameterDTO implements Serializable {

    /**
     * 基础值
     */
    private Integer basicsValue;

    /**
     * HashMap<连续登入天数,对应积分值>
     */
    private TreeMap<Integer, Integer> extendValue;

    private List<ConsumeJsonDTO> consumeJson;

}
