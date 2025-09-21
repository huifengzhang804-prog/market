package com.medusa.gruul.addon.member.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.addon.member.model.dto.PayDTO;
import com.medusa.gruul.addon.member.model.vo.PaidMemberRightsVO;
import com.medusa.gruul.addon.member.mp.entity.PaidMember;
import com.medusa.gruul.addon.member.mp.service.IPaidMemberService;
import com.medusa.gruul.addon.member.service.ManagePaidMemberService;
import com.medusa.gruul.addon.member.service.MovePaidMemberService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.payment.api.enums.FeeType;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import com.medusa.gruul.user.api.enums.paid.PaidMemberRabbit;
import com.medusa.gruul.user.api.model.dto.paid.PaidMemberDealDTO;
import com.medusa.gruul.user.api.model.dto.paid.PaidRuleJsonDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 移动端付费会员实现层
 *
 * @author xiaoq
 * @Description MovePaidMemberServiceImpl.java
 * @date 2022-11-17 15:04
 */
@Service
@RequiredArgsConstructor
public class MovePaidMemberServiceImpl implements MovePaidMemberService {

    private final IPaidMemberService paidMemberService;

    private final PaymentRpcService paymentRpcService;

    private final ManagePaidMemberService managePaidMemberService;

    @Override
    public PayResult openPaidMember(PayDTO pay) {
        PaidMember paidMember = paidMemberService.lambdaQuery().eq(BaseEntity::getId, pay.getId()).one();
        if (paidMember == null) {
            throw new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, "当前会员级别不存在");
        }
        Optional<PaidRuleJsonDTO> paidRuleJsonOptional = paidMember.getPaidRuleJson().stream().filter(bean -> pay.getPaidRuleId().equals(bean.getId())).findAny();
        PaidRuleJsonDTO paidRuleJson = paidRuleJsonOptional.orElseThrow(() -> new GlobalException("当前付费规则不存在"));

        //封装开通会员信息
        PaidMemberDealDTO paidMemberDeal = new PaidMemberDealDTO();
        paidMemberDeal.setPaidMemberId(paidMember.getId());
        paidMemberDeal.setRankCode(paidMember.getRankCode());
        paidMemberDeal.setUserId(ISecurity.userMust().getId());
        paidMemberDeal.setPayType(pay.getPayType());
        paidMemberDeal.setPayAmount(pay.getPayAmount());
        paidMemberDeal.setPaidRuleJson(paidRuleJson);
        return initPayResult(paidMemberDeal);
    }

    /**
     * 移动端获取付费会员级别信息
     */
    @Override
    public List<PaidMemberRightsVO> getRankPaidMemberInfo() {
        List<PaidMemberRightsVO> list = paidMemberService.getPaidMemberList();
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        managePaidMemberService.disposeRights(list);
        return list;
    }


    /**
     * 初始化支付信息
     *
     * @param paidMemberDeal 付费会员交易信息
     * @return PayResult 支付结果
     */
    private PayResult initPayResult(PaidMemberDealDTO paidMemberDeal) {
        PaymentInfoDTO paymentInfo = new PaymentInfoDTO();
        paymentInfo.setOrderNum(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextUUID(new PaidMember()));
        paymentInfo.setTotalFee(paidMemberDeal.getPayAmount());
        paymentInfo.setPayType(paidMemberDeal.getPayType());
        paymentInfo.setUserId(paidMemberDeal.getUserId());
        paymentInfo.setSubject("用户开通付费会员");
        paymentInfo.setBody("用户开通付费会员{}".concat(paymentInfo.getOrderNum()));
        //附加所使用规则json
        paymentInfo.setAttach(JSON.toJSONString(paidMemberDeal));

        paymentInfo.setExchange(PaidMemberRabbit.EXCHANGE);
        paymentInfo.setRouteKey(PaidMemberRabbit.PAID_MEMBER_DREDGE.routingKey());
        paymentInfo.setSeconds(30 * 60L);
        paymentInfo.setFeeType(FeeType.CNY);
        paymentInfo.setTerminalIp(ISystem.ipMust());
        paymentInfo.setShopId(ISystem.shopIdMust());
        paymentInfo.setPayPlatform(ISystem.platformMust());
        paymentInfo.setOpenId(ISecurity.userMust().getOpenid());
        paymentInfo.setAuthCode(null);
        paymentInfo.setWapUrl(null);
        paymentInfo.setWapName(null);
        return paymentRpcService.payRequest(paymentInfo);
    }
}
