package com.medusa.gruul.live.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.live.api.enums.AuditStatus;
import lombok.Data;

/**
 * @author miskw
 * @date 2022/11/12
 * 获取直播商品展示接收参数
 */
@Data
public class GetGoodsDto extends Page<Object> {
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 直播审核状态
     */
    private AuditStatus auditStatus;

    /**
     * 价格区间
     *  - 分割价格
     */
    private String salePrice;

}
