package com.medusa.gruul.search.service.model.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.easyes.core.biz.EsPageInfo;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 张治保
 * @since 2023/9/25
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SearchResp<T> implements Serializable {

    /**
     * 聚合结果
     */
    private Map<String, Object> aggregations;

    /**
     * 分页结果
     */
    private EsPageInfo<T> page;
}
