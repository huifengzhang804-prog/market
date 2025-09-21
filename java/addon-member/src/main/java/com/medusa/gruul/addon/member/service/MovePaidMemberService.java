package com.medusa.gruul.addon.member.service;

import com.medusa.gruul.addon.member.model.dto.PayDTO;
import com.medusa.gruul.addon.member.model.vo.PaidMemberRightsVO;
import com.medusa.gruul.payment.api.model.pay.PayResult;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2022-11-17 15:04
 */
public interface MovePaidMemberService {
    /**
     * 开通会员
     *
     * @param pay 支付信息
     * @return 业务支付结果
     */
    PayResult openPaidMember(PayDTO pay);

    /**
     * 付费会员级别列表----移动端
     *
     * @return List<PaidMemberRightsVO>
     */
    List<PaidMemberRightsVO> getRankPaidMemberInfo();
}
