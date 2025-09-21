package com.medusa.gruul.addon.freight.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsExpress;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 *  物流公司param
 *
 * @author xiaoq
 * @Description LogisticsExpressParam.java
 * @date 2022-06-20 13:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LogisticsExpressParam extends Page<LogisticsExpress> {
    private static final long serialVersionUID = -5528572194450498427L;
    private Long shopId;
}
