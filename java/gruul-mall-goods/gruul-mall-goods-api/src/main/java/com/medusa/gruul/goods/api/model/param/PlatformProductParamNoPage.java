package com.medusa.gruul.goods.api.model.param;

import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import lombok.Data;

/**
 * @Description: 平台获取商品查询信息
 * @Author: xiaoq
 * @Date : 2022-04-21 15:47
 */
@Data
public class PlatformProductParamNoPage{

    /**
     * 所属平台类目id
     */
    private Long platformCategoryId;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品状态
     */
    private ProductStatus status;

    /**
     * 商品类型
     */
    private ProductType productType;

    /**
     * 销售类型
     */
    private SellType sellType;

    /**
     * 店铺id
     */
    private Long shopId;


    /**
     * 创建开始时间
     */
    private String createBeginTime;

    /**
     * 创建结束时间
     */
    private String createEndTime;

}
