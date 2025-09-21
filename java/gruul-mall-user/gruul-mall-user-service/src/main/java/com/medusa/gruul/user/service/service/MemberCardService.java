package com.medusa.gruul.user.service.service;

import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.user.api.enums.RightsType;
import com.medusa.gruul.user.api.model.dto.paid.PaidMemberDealDTO;
import com.medusa.gruul.user.api.model.vo.MemberAggregationInfoVO;
import com.medusa.gruul.user.service.mp.entity.UserAccount;
import com.medusa.gruul.user.service.mp.entity.UserMemberCard;
import io.vavr.control.Option;

import java.util.Map;
import java.util.Set;

/**
 * 会员卡服务层
 *
 * @author xiaoq
 * @Description MemberCardService.java
 * @date 2022-11-17 10:58
 */
public interface MemberCardService {

    /**
     * 会员中心
     *
     * @param userId 用户id
     * @return 会员中心聚合vo
     */
    MemberAggregationInfoVO getMemberCentre(Long userId);


    /**
     * 用户开通付费会员卡
     *
     * @param transactionId  交易支付流水号
     * @param paidMemberDeal 付费会员信息
     * @param businessParams 支付回调信息
     */
    void userPayPaidMember(String transactionId, PaidMemberDealDTO paidMemberDeal, PaymentInfoDTO businessParams);


    /**
     * 检测更新用户免费会员卡信息
     *
     * @param user 用户信息
     * @return 用户最新免费会员卡
     */
    Option<UserMemberCard> checkFreeMemberCardInfo(UserAccount user);

    /**
     * 根据用户 ID 集合和权限类型获取用户权限
     *
     * @param userIds    用户 ID 集合
     * @param rightsType 权限类型
     * @return 包含用户权限的 Map，键为用户 ID，值为布尔值，表示是否具有指定权限类型
     */
    Map<Long, Boolean> getUserRights(Set<Long> userIds, RightsType rightsType);

    /**
     * 查询会员等级数据是否在使用中
     * @param memberId
     * @return
     */
    boolean queryMemberInUse(Long memberId);
}
