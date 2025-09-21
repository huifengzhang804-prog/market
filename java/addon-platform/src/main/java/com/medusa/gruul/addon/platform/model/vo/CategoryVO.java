package com.medusa.gruul.addon.platform.model.vo;

import com.medusa.gruul.addon.platform.mp.entity.PlatformCategory;
import com.medusa.gruul.common.custom.aggregation.classify.enums.CategoryLevel;
import lombok.Data;

import java.util.List;

/**
 * @Description: 类目Vo
 * @Author: xiaoq
 * @Date : 2022-04-18 14:46
 */
@Data
public class CategoryVO {
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 上机分类的编号：0表示一级分类
     */
    private Long parentId;

    /**
     * 类目id(数据传输用)
     */
    private Long categoryId;

    /**
     * 分类级别：0->1级；1->2级
     */
    private CategoryLevel level;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     *商品数量
     */
    private Long productNumber;
    /**
     * 广告
     */
    private List<PlatformCategory.Ad> ads;

    /**
     *二级分类
     */
    private List<CategorySecondVO> secondCategoryVos;


}
