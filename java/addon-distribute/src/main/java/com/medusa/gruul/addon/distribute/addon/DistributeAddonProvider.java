package com.medusa.gruul.addon.distribute.addon;

import com.medusa.gruul.goods.api.model.param.EarningParam;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.overview.api.model.OverViewStatementResp;
import jakarta.validation.constraints.NotNull;

/**
 * @author 张治保 date 2023/6/10
 */
public interface DistributeAddonProvider {
    String FILTER = "DISTRIBUTE";

    /**
     * 创建对账单 (暂时只用于 分销插件)
     *
     * @param orderCompleted 订单完成数据
     * @return 分销对账单数据
     */
    OverViewStatementResp createStatements(OrderCompletedDTO orderCompleted);


    /**
     * 查询用户分销码
     *
     * @param userId 用户 id
     * @return 分销码
     */
    String distributorCode(@NotNull Long userId);

    /**
     * 分销预计赚
     *
     * @param param 预计赚请求参数
     * @return 预计赚金额
     */
    Long earning(EarningParam param);
}
