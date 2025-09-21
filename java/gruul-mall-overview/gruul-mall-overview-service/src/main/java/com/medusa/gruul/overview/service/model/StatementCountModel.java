package com.medusa.gruul.overview.service.model;

import com.medusa.gruul.common.model.enums.ChangeType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 张治保
 * date 2022/11/24
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class StatementCountModel {

    /**
     * 收入支出类型
     */
    private ChangeType changeType;

    /**
     * 类型对应金额统计
     */
    private long amount;

    /**
     * 对应的条数
     */
    private long amountCount;
}
