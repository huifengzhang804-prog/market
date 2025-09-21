package com.medusa.gruul.storage.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 商品规格组
 *
 * @author 张治保
 * @since 2022-07-12
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = {"shopId", "productId", "name"}, callSuper = true)
@TableName(value = "t_storage_spec_group", excludeProperty = "deleted")
public class StorageSpecGroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 所属店铺id
     */
    private Long shopId;

    /**
     * 所属商品id
     */
    private Long productId;

    /**
     * 规格组名
     */
    @TableField("`name`")
    private String name;

    /**
     * 排序
     */
    @TableField("`order`")
    private Integer order;

    /**
     * 规格组下的规格列表
     */
    @TableField(exist = false)
    private List<StorageSpec> specs;

}
