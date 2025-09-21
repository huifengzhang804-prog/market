package com.medusa.gruul.search.service.es.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldType;

import java.util.Set;

/**
 * 商品搜索历史
 *
 * @author 张治保
 * date 2022/12/15
 */
@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
@IndexName(value = "product_search_history", keepGlobalPrefix = true)
public class EsProductSearchHistoryEntity extends EsBaseEntity {

    /**
     * 用户id 可能为空 记录用户搜索历史
     */
    private Long userId;

    /**
     * 搜索关键词
     */
    @IndexField(fieldType = FieldType.KEYWORD, analyzer = Analyzer.IK_SMART)
    private String keyword;

    /**
     * 搜索关键词分词结果
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private Set<String> words;


}
