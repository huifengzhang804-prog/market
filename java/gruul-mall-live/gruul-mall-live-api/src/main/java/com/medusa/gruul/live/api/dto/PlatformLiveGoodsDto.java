package com.medusa.gruul.live.api.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.live.api.enums.AuditStatus;
import lombok.Data;

/**
 * @author miskw
 * @date 2022/11/16
 * @Describe 平台查询所有直播商品
 */
@Data
public class PlatformLiveGoodsDto extends Page<Object> {
    /**
     * 商品状态
     */
    private AuditStatus auditStatus;

    /**
     * 商品名称、店铺名称
     */
    private String keywords;


}
