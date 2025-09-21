package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 三方采集商品数据
 *
 * @author jipeng
 * @since 2024/11/1
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_collect_product_data")
public class CollectProductData extends BaseEntity {
    /**
     * 采集数据来源 taobao jd  alibaba
     */
    private String type;
    /**
     * 采集商品Id
     */
    private String termId;
    /**
     * 商品采集内容
     */
    private String content;
}
