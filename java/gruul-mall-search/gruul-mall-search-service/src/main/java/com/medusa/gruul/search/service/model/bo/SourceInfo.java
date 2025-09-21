package com.medusa.gruul.search.service.model.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 素材源信息
 *
 * @author 张治保
 * @since 2023/9/22
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class SourceInfo implements Serializable {

    /**
     * 素材名称
     */
    private String name;

    /**
     * 素材格式
     */
    private String format;

    /**
     * 图片素材 宽（像素值）
     */
    private Integer width;

    /**
     * 图片素材 高（像素值）
     */
    private Integer height;

}
