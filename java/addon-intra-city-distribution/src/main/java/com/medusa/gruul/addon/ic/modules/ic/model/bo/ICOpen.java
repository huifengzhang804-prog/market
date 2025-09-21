package com.medusa.gruul.addon.ic.modules.ic.model.bo;

import com.medusa.gruul.addon.ic.modules.ic.model.vo.DeliverItemPriceVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * @since 2024/8/29
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ICOpen {
    /**
     * UU 跑腿平台获取的价格 token
     */
    private String uuptPriceToken;

    /**
     * priceToken过期时间
     */
    private LocalDateTime priceTokenExpireTime;

    /**
     * UU 跑腿开放平台回调时对当前回调状态的描述
     */
    private String uuptDesc;

    /**
     * UU跑腿 配送价格
     */
    private DeliverItemPriceVO uuptPrice;


}
