package com.medusa.gruul.payment.api.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 储值订单历史param
 *
 * @author xiaoq
 * @Description SavingsOrderHistoryParam.java
 * @date 2022-12-22 16:47
 */
@Getter
@Setter
@ToString
public class SavingsOrderHistoryParam extends Page<Object> {

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 会员昵称
     */
    private String userNickname;
}
