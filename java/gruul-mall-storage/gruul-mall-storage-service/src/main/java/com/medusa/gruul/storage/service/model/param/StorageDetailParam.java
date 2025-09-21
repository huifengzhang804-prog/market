package com.medusa.gruul.storage.service.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * StorageDetailParam.java
 *
 * @author xiaoq
 * @Description StorageDetailParam.java
 * @date 2023-07-27 15:15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StorageDetailParam extends Page<Object> {

    private Long id;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品id
     */
    private Long productId;

    /**
     * skuId
     */
    private Long skuId;


    /**
     * 变化类型
     */
    private StockChangeType stockChangeType;


    /**
     * 销售类型
     */
    private SellType sellType;

    /**
     * 关联订单号
     */
    private String orderNo;

    /**
     * 变更人姓名
     */
    private String changeName;

    /**
     * 变更人手机号
     */
    private String changePhone;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;


    /**
     * 是否出库
     */
    private Boolean isOutbound;
    /**
     * 导出的ids
     */
    private List<Long> exportIds;

    private Long shopId;


}
