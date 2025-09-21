package com.medusa.gruul.live.service.model.vo;

import com.medusa.gruul.live.api.enums.AuditStatus;
import lombok.Data;


/**
 * @author miskw
 * @date 2022/11/9
 * @describe 审核商品返回对象
 */
@Data
public class LiveGoodsExamineVo {
    /**
     * 审核状态
     */
    private AuditStatus auditStatus;

    /**
     * 租户Id 店铺标识符
     */
    private String tenantId;

    /**
     * 商品详情页的小程序路径，路径参数存在 url 的，该参数的值需要进行 encode 处理再填入
     */
    private String url;

    /**
     * 商品Id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * sku价格集
     */
    private String salePrices;


}
