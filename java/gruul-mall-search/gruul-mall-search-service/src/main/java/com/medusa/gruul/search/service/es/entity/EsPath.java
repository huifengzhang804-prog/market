package com.medusa.gruul.search.service.es.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.rely.FieldType;

/**
 * @author 张治保
 * @since 2023/9/23
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class EsPath {

    /**
     * 路径层级
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private Integer level;

    /**
     * 路径 id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String id;
}
