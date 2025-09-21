package com.medusa.gruul.goods.service.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 张治保
 * date 2022/4/22
 */
@Getter
@Setter
@ToString
public class CategorySortDTO {

    @NotNull
    private Long parentId;

    @NotNull
    @Size(min = 1)
    private List<Long> sortedIds;

}
