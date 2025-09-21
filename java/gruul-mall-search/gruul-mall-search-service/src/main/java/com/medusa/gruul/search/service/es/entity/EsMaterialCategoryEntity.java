package com.medusa.gruul.search.service.es.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.util.List;

/**
 * @author 张治保
 * @since 2023/9/22
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@IndexName(value = "material_category", keepGlobalPrefix = true)
public class EsMaterialCategoryEntity extends EsBaseEntity {

    @IndexId(type = IdType.CUSTOMIZE)
    private String id;

    /**
     * 父级分类 id,一级分类的这个值为空
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String parentId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 分类名称 长度为 8
     */
    @IndexField(fieldType = FieldType.KEYWORD_TEXT, analyzer = Analyzer.PINYIN, searchAnalyzer = Analyzer.PINYIN)
    private String name;

    /**
     * 节点 path 信息
     */
    @IndexField(fieldType = FieldType.NESTED, nestedClass = EsPath.class)
    private List<EsPath> paths;


    /**
     * 是否有子节点
     */
    @IndexField(exist = false)
    private Boolean hasChildren;

    /**
     * 是否历史选择
     */
    @IndexField(exist = false)
    private Boolean selectHistory;

    /**
     * 子节点
     */
    @IndexField(exist = false)
    private List<EsMaterialCategoryEntity> children;
}
