package com.medusa.gruul.search.api.model;

import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.search.api.enums.CategoryCountType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author 张治保
 * @since 2024/3/18
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class CategoryCountParam implements BaseDTO {

    /**
     * 分类统计类型 {@link CategoryCountType}
     * 平台 ｜ 店铺
     */
    @NotNull
    private CategoryCountType type;

    /**
     * 是否是正常可用商品
     */
    private Boolean normal = Boolean.FALSE;

    /**
     * 店铺id 店铺分类商品统计时必传
     */
    private Long shopId;

    /**
     * 一级分类id 为空时查询所有
     */
    private Set<Long> firstIds;



    @Override
    public void validParam() {
        if (CategoryCountType.SHOP == type && shopId == null) {
            throw new IllegalArgumentException("shopId is null");
        }
    }
}
