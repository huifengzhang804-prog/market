package com.medusa.gruul.overview.service.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.overview.api.model.OverViewStatementResp;
import com.medusa.gruul.overview.api.model.OverviewConstant;

/**
 * @author 张治保
 * date 2023/6/9
 */
@AddonSupporter(id = OverviewConstant.OVERVIEW_STATEMENT_SUPPORT_ID)
public interface OverviewStatementSupporter {


    /**
     * 创建对账单 (暂时只用于 分销插件)
     *
     * @param orderCompleted 订单完成数据
     * @return 分销对账单数据
     */
    @AddonMethod(returnType = OverViewStatementResp.class)
    OverViewStatementResp createStatements(OrderCompletedDTO orderCompleted);
}
