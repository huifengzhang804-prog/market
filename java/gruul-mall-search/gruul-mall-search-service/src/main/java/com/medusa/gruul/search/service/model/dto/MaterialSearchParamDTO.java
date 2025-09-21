package com.medusa.gruul.search.service.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dromara.easyes.core.biz.OrderByParam;

import java.io.Serializable;
import java.util.List;

/**
 * @author 张治保
 * @since 2023/9/23
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MaterialSearchParamDTO implements Serializable {

    /**
     * 素材所属分类id
     */
    private String categoryId;

    /**
     * 素材尺寸
     */
    private String imgSize;

    /**
     * 素材格式
     */
    private String format;

    /**
     * 素材名称
     */
    private String name;

    /**
     * 分页页码
     */
    @NotNull
    @Min(1)
    private Integer current = 1;

    /**
     * 分页大小
     */
    @NotNull
    @Min(0)
    @Max(500)
    private Integer size = 20;

    /**
     * 排序参数
     */
    private List<OrderByParam> orders;
}
