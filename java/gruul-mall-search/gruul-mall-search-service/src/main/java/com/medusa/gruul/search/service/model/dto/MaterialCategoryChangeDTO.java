package com.medusa.gruul.search.service.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * @author 张治保
 * @since 2023/9/25
 */
@Getter
@Setter
@ToString
public class MaterialCategoryChangeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2134572800469566685L;
    /**
     * 素材分类 id
     */
    private String categoryId;

    /**
     * 素材 id
     */
    @Size(min = 1)
    private Set<String> materialIds;
}
