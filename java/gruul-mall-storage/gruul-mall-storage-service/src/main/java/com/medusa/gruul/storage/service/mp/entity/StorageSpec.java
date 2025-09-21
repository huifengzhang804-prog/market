package com.medusa.gruul.storage.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 
 * 商品规格
 * 
 *
 * @author 张治保
 * @since 2022-07-12
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_storage_spec", excludeProperty = "deleted")
public class StorageSpec extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 商品id
     */
    private Long productId;
    /**
     * 规格组id
     */
    private Long groupId;

    /**
     * 规格名
     */
    @TableField("`name`")
    private String name;

    /**
     * 排序
     */
    @TableField("`order`")
    private Integer order;
}
