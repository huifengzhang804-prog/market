package com.medusa.gruul.goods.api.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 三级类目
 * @author: WuDi
 * @date: 2022/9/23
 */
@Data
@Accessors(chain = true)
public class PlatformCategoryVo implements Serializable {

    /**
     * 三级二级平台分类idMap
     */
    Map<Long, Long> thirdSecondMap;
    /**
     * 二级一级平台分类idMap
     */
    Map<Long, Long> secondFirstMap;

}
