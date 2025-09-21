package com.medusa.gruul.addon.rebate.util;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.addon.rebate.model.bo.UserRebatePercent;
import com.medusa.gruul.addon.rebate.mp.entity.RebateConf;
import com.medusa.gruul.addon.rebate.mp.entity.RebateUsersExtendValue;
import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.api.model.vo.MemberAggregationInfoVO;

/**
 * 返利工具类
 *
 * @author jinbu
 */
public interface RebateUtil {

    /**
     * 获取返利百分比
     *
     * @param conf    返利配置
     * @param vipCard 用户会员卡信息
     * @return 返利百分比 1.返利百分比 2.返利支付的最高百分比
     */
    static UserRebatePercent rebatePercent(RebateConf conf, MemberAggregationInfoVO vipCard) {
        MemberType vipType = vipCard.getMemberType();
        Integer rankCode = vipCard.getCurrentMemberVO().getRankCode();
        UserRebatePercent rebatePercent = new UserRebatePercent();
        for (RebateUsersExtendValue value : CollUtil.emptyIfNull(conf.vipRebateConfigs())) {
            if (!value.getMemberType().equals(vipType) || !value.getRankCode().equals(rankCode)) {
                continue;
            }
            return rebatePercent.setRebate(value.getRebatePercentage())
                    .setPay(value.getRebatePaymentPercentage())
                    .setWithdrawalThreshold(value.getWithdrawalThreshold() == null ? Long.MAX_VALUE : value.getWithdrawalThreshold());
        }
        return rebatePercent;
    }


}
