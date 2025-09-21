package com.medusa.gruul.order.service.model.vo;


import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.order.api.entity.OrderEvaluate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * 商品详情页用户评价
 * @author wudi
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductEvaluateOverviewVO {

    /**
     * 总条数
     */
    private Long totalCount;
    /**
     * 好评总数
     */
    private Long praiseCount;
    /**
     * 有内容的评价总数
     */
    private Long contentCount;
    /**
     * 有图片的评论总数
     */
    private Long mediaCount;
    /**
     * 评价概况展示的评价
     */
    private OrderEvaluate evaluate;


    /**
     * 获取好评率
     *  （好评数 x 100）/ 总数 保留两个小数
     * @return 好评率百分比
     */
    public String getPraiseRatio(){
        Long total = this.getTotalCount();
        if (total.equals(0L)){
            return String.valueOf(CommonPool.NUMBER_ZERO);
        }
        return BigDecimal.valueOf(praiseCount)
                .multiply(CommonPool.BIG_DECIMAL_UNIT_CONVERSION_HUNDRED)
                .divide(BigDecimal.valueOf(totalCount),2, RoundingMode.HALF_UP)
                .toPlainString();
    }

}
