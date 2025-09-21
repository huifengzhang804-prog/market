package com.medusa.gruul.user.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.user.service.mp.entity.UserFootMark;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 用户足迹查询条件
 * @author WuDi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserFootMarkQueryDTO extends Page<UserFootMark> {

    /**
     * 日期
     */
    private String footMarkDate;

    /**
     * 月份
     */
    private Integer month;

    /**
     * 开始日期
     */
    private String startDate;
    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 三级分类id
     */
    private Long platformCategoryId;

    /**
     * 商品名称
     */
    private String productName;
}
