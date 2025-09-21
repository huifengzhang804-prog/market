package com.medusa.gruul.order.api.model;

import com.medusa.gruul.common.model.constant.CommonPool;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author 张治保
 * date 2022/10/22
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderEvaluateCountDTO implements Serializable {

    /**
     * 总评价分数
     */
    private Long totalScore;

    /**
     * 总评价条数
     */
    private Long totalCount;

    private final static String DEFAULT_SHOP_SCORE = "5.0";

    public String score() {
        Long totalCount = getTotalCount();
        Long totalScore = getTotalScore();
        if (totalCount == null || totalCount == 0 || totalScore == null) {
            return DEFAULT_SHOP_SCORE;
        }
        return BigDecimal.valueOf(totalScore)
                .divide(BigDecimal.valueOf(totalCount), CommonPool.NUMBER_ONE, RoundingMode.HALF_UP)
                .toPlainString();
    }

    public static String defaultScore() {
        return DEFAULT_SHOP_SCORE;
    }


}
