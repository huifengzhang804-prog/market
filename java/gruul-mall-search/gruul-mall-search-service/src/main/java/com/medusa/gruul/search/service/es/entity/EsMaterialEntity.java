package com.medusa.gruul.search.service.es.entity;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldType;

/**
 * @author 张治保
 * @since 2023/9/22
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString(callSuper = true)
@IndexName(value = "material", keepGlobalPrefix = true)
public class EsMaterialEntity extends EsBaseEntity {

    private static final String SIZE_TEMPLATE = "{}x{}";

    /**
     * 素材分类名称
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String categoryName;
    /**
     * 素材分类id
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String categoryId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 素材 url
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String url;

    /**
     * 素材名称
     */
    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.PINYIN, searchAnalyzer = Analyzer.PINYIN)
    private String name;

    /**
     * 素材格式
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String format;

    /**
     * 图片素材 宽x高（像素值）
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String size;


    /**
     * 渲染图片尺寸
     *
     * @param width  图片宽度 像素值
     * @param height 图片高度 像素值
     * @return 渲染后的尺寸 string
     */
    public static String renderSize(Integer width, Integer height) {
        return StrUtil.format(SIZE_TEMPLATE, width, height);
    }

}
