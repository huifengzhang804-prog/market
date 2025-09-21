package com.medusa.gruul.common.custom.aggregation.decoration.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 *
 *装修 propertiesVO
 *
 * @author xiaoq
 * @description PropertiesVO.class
 * @date 2022-10-31 20:26
 */
@Getter
@Setter
@ToString
public class PropertiesVO {

    private Long id;

    List<Object> properties;
}
