package com.medusa.gruul.addon.bargain.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.bargain.model.enums.BargainStatus;
import com.medusa.gruul.addon.bargain.mp.entity.BargainOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 砍价订单查询参数
 * @author wudi
 */
@Getter
@Setter
@ToString
public class BargainOrderQueryDTO extends Page<BargainOrder> {

    /**
     * 砍价状态
     */
    private BargainStatus bargainStatus;

    /**
     * 关键字
     */
    private String keyword;

}
