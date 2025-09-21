package com.medusa.gruul.addon.store.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 店铺店员
 *
 * @author xiaoq
 * @Description 店铺店员表
 * @date 2023-03-14 14:18
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_shop_assistant", autoResultMap = true)
public class ShopAssistant extends BaseEntity {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 门店id
     */
    private Long storeId;


    /**
     * 店员手机号
     */
    private String assistantPhone;


    /**
     * 排序
     */
    private Integer sort;


}
