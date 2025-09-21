package com.medusa.gruul.addon.ic.modules.opens.judanke.api;

import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.addon.ic.modules.opens.judanke.JudankeResponse;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.finance.BetBalanceResp;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.finance.GetRechargeQrcodeParam;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.finance.GetRechargeQrcodeResp;

/**
 * @author 张治保
 * @since 2024/8/7
 */
public interface IFinanceApi extends IBaseJudankeApi {

    /**
     * <a href="https://open.judanke.cn/docs/142/47">查询账户余额</a>
     *
     * @return 账户余额
     */
    default JudankeResponse<BetBalanceResp> getBalance() {
        return request(null, new TypeReference<>() {
        });
    }

    /**
     * <a href="https://open.judanke.cn/docs/142/47">查询账户余额</a>
     *
     * @return 账户余额
     */
    default JudankeResponse<GetRechargeQrcodeResp> getRechargeQrcode(GetRechargeQrcodeParam param) {
        return request(param, new TypeReference<>() {
        });
    }

    @Override
    default String apiPrefix() {
        return "/Finance/";
    }

}
