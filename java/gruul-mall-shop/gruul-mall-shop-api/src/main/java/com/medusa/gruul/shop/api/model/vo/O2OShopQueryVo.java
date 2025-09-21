package com.medusa.gruul.shop.api.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class O2OShopQueryVo {
    /**
     * 经度
     */
    private Double longitude;
    /**
     * 纬度
     */
    private Double latitude;
    /**
     * 排序
     */
    private Boolean sortAsc;
    /**
     * 显示头部店铺
     */
    private List<Long> showHeaderShopIds;
    /**
     * 更多数量
     */
    private Integer moreCount;
}
