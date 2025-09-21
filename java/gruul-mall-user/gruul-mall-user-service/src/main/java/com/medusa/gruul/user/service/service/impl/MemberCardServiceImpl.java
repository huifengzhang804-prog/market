package com.medusa.gruul.user.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Maps;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.*;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.common.model.message.OverviewStatementDTO;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.constant.SecurityConst;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.user.api.constant.UserConstant;
import com.medusa.gruul.user.api.enums.*;
import com.medusa.gruul.user.api.model.dto.BalanceChangeDTO;
import com.medusa.gruul.user.api.model.dto.paid.PaidMemberDealDTO;
import com.medusa.gruul.user.api.model.vo.*;
import com.medusa.gruul.user.service.model.dto.UserVirtualDeliverDTO;
import com.medusa.gruul.user.service.model.vo.RankedMemberVO;
import com.medusa.gruul.user.service.mp.entity.*;
import com.medusa.gruul.user.service.mp.service.*;
import com.medusa.gruul.user.service.mp.service.impl.MemberPurchaseHistoryService;
import com.medusa.gruul.user.service.service.IUserBalanceHistoryManageService;
import com.medusa.gruul.user.service.service.MemberCardService;
import com.medusa.gruul.user.service.service.addon.UserAddonSupporter;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xiaoq
 * Description MemberCardServiceImpl.java
 * date 2022-11-17 10:59
 */
@Service
@RequiredArgsConstructor
public class MemberCardServiceImpl implements MemberCardService {

    private final IUserMemberCardService userMemberCardService;

    private final IUserAccountService userAccountService;

    private final IUserFreeMemberService userFreeMemberService;

    private final RabbitTemplate rabbitTemplate;

    private final UserAddonSupporter userAddonSupporter;

    private final Executor globalExecutor;

    private final IMemberRightsService memberRightsService;

    private final MemberPurchaseHistoryService memberPurchaseHistoryService;

    private final IUserBalanceHistoryManageService userBalanceHistoryManageService;

    private final IUserMemberRelevancyRightsService userMemberRelevancyRightsService;


    @Override
    public MemberAggregationInfoVO getMemberCentre(Long userId) {
        userId = Option.of(userId).getOrElse(() -> ISecurity.userMust().getId());
        UserAccount user = userAccountService.getUserMemberInfoByUserId(userId);
        if (user == null) {
            throw new GlobalException("当前用户信息不存在");
        }
        List<UserMemberCard> memberCards = user.getMemberCards();
        // 用户不存在付费会员卡信息 返回免费会员信息
        if (CollUtil.isEmpty(memberCards)) {
            MemberAggregationInfoVO memberAggregationInfoVO = freeMemberAggregationAssemble(user);
            UserFreeMember userFreeMember = userFreeMemberService.lambdaQuery()
                    .select(UserFreeMember::getNeedValue, UserFreeMember::getLabelJson)
                    .eq(UserFreeMember::getRankCode, memberAggregationInfoVO.getCurrentMemberVO().getRankCode())
                    .one();
            memberAggregationInfoVO.setSubordinateGrowthValue(
                    Option.of(userFreeMember).map(UserFreeMember::getNeedValue).getOrElse(0L)
            );
            memberAggregationInfoVO.setMemberLabel(
                    Option.of(userFreeMember).map(UserFreeMember::getLabelJson).getOrNull()
            );
            return memberAggregationInfoVO;

        }
        Map<Long, UserMemberCard> memberIdCardMap = memberCards.stream().collect(Collectors.toMap(UserMemberCard::getMemberId, v -> v));
        //获取正在使用的用户付费会员卡 (用户会员卡中最高等级的会员级别)
        PaidMemberInfoVO paidMember = userAddonSupporter.getPaidMemberInfo(memberIdCardMap.keySet());
        // 用户付费会员卡因异常原因到期 返回免费会员信息
        if (paidMember == null) {
            MemberAggregationInfoVO memberAggregationInfoVO = freeMemberAggregationAssemble(user);
            UserFreeMember userFreeMember = userFreeMemberService.lambdaQuery()
                    .select(UserFreeMember::getNeedValue, UserFreeMember::getLabelJson)
                    .eq(UserFreeMember::getRankCode, memberAggregationInfoVO.getCurrentMemberVO().getRankCode())
                    .one();
            memberAggregationInfoVO.setMemberLabel(userFreeMember.getLabelJson());
            return memberAggregationInfoVO;
        }
        UserMemberCard userMemberCard = memberIdCardMap.get(paidMember.getMemberId());

        //会员权益
        List<MemberBasicsRelevancyRightsVO> basicMemberRights = paidMember.getMemberBasicsRelevancyRightsList();
        //付费会员权益为空 使用免费会员权益 但是会员卡显示为付费会员
        if (CollUtil.isEmpty(basicMemberRights)) {

            MemberAggregationInfoVO freeMemberRight = freeMemberAggregationAssemble(user);
            CurrentMemberVO currentMemberVO = freeMemberRight.getCurrentMemberVO();
            currentMemberVO.setMemberName(paidMember.getPaidMemberName());
            currentMemberVO.setRankCode(paidMember.getRankCode());
            currentMemberVO.setMemberCardId(userMemberCard.getId());
            currentMemberVO.setMemberCardValidTime(userMemberCard.getMemberCardValidTime());
            freeMemberRight.setMemberLabel(paidMember.getMemberLabel());
            return freeMemberRight.setMemberType(MemberType.PAID_MEMBER);
        }

        Map<Long, MemberBasicsRelevancyRightsVO> basicRightMap = basicMemberRights.stream().collect(Collectors.toMap(MemberBasicsRelevancyRightsVO::getMemberRightsId, v -> v));
        //权益列表
        List<MemberRights> memberRights = memberRightsService.lambdaQuery()
                .in(MemberRights::getId, basicRightMap.keySet())
                .eq(MemberRights::getRightsSwitch, Boolean.TRUE)
                .list();

        // 处理付费会员到期会员卡信息
        disposeExpireMemberCard(paidMember, userId);
        Map<RightsType, List<RelevancyRightsVO>> rightsTypeList = memberRights.stream()
                .filter(memberRight -> basicRightMap.containsKey(memberRight.getId()))
                .map(memberRight -> {
                    return new RelevancyRightsVO()
                            .setExtendValue(basicRightMap.get(memberRight.getId()).getExtendValue())
                            .setRightsName(memberRight.getRightsName())
                            .setRightsType(memberRight.getRightsType())
                            .setMemberRightsId(memberRight.getId());
                })
                .collect(Collectors.groupingBy(RelevancyRightsVO::getRightsType));
        return new MemberAggregationInfoVO()
                .setUserNickname(user.getUserNickname())
                .setUserHeadPortrait(user.getUserHeadPortrait())
                .setGrowthValue(user.getGrowthValue())
                .setMemberType(MemberType.PAID_MEMBER)
                .setMemberLabel(paidMember.getMemberLabel())
                .setCurrentMemberVO(
                        new CurrentMemberVO()
                                .setMemberCardId(userMemberCard.getId())
                                .setMemberCardValidTime(userMemberCard.getMemberCardValidTime())
                                .setRankCode(userMemberCard.getRankCode())
                                .setRelevancyRights(rightsTypeList)
                                .setMemberName(paidMember.getPaidMemberName())
                );

    }


    /**
     * 用户开通付费会员
     */
    @Override
    @Redisson(name = UserConstant.USER_OPEN_PAID_MEMBER_CARD, key = "#paidMemberDeal.userId")
    @Transactional(rollbackFor = Exception.class)
    public void userPayPaidMember(String transactionId, PaidMemberDealDTO paidMemberDeal, PaymentInfoDTO businessParams) {
        //获取续费有效时长 /天
        Integer durationDays = paidMemberDeal.getPaidRuleJson().getEffectiveDurationType().getValue();
        // 查看当前用户开通得会员卡是否存在
        Long userId = paidMemberDeal.getUserId();
        UserMemberCard memberCard = userMemberCardService.lambdaQuery()
                .eq(UserMemberCard::getUserId, userId)
                .eq(UserMemberCard::getMemberType, MemberType.PAID_MEMBER)
                .eq(UserMemberCard::getMemberCardStatus, MemberCardStatus.NORMAL)
                .eq(UserMemberCard::getRankCode, paidMemberDeal.getRankCode())
                .eq(UserMemberCard::getMemberId, paidMemberDeal.getPaidMemberId())
                .one();
        //交易类型
        TransactionType transactionType;
        //用于计算会员过期时间
        LocalDate memberExpiredDate;
        if (memberCard == null || memberCard.getMemberCardValidTime().isBefore(LocalDate.now())) {
            // 新增会员卡
            // 会员已过期
            if (memberCard != null && memberCard.getMemberCardValidTime().isBefore(LocalDate.now())) {
                //更新为已过期状态
                userMemberCardService.lambdaUpdate()
                        .set(UserMemberCard::getMemberCardStatus, MemberCardStatus.EXPIRE)
                        .eq(BaseEntity::getId, memberCard.getId())
                        .update();
            }
            transactionType = TransactionType.PAID_MEMBER_OPEN;
            memberExpiredDate = LocalDate.now().plusDays(durationDays);
            userMemberCardService.save(
                    new UserMemberCard()
                            .setMemberCardStatus(MemberCardStatus.NORMAL)
                            .setMemberType(MemberType.PAID_MEMBER)
                            .setMemberCardValidTime(memberExpiredDate)
                            .setMemberId(paidMemberDeal.getPaidMemberId())
                            .setUserId(userId)
                            .setOpenType(OpenType.PAID_BUY)
                            .setRankCode(paidMemberDeal.getRankCode())
            );

        } else {
            //原有会员卡续费
            transactionType = TransactionType.PAID_MEMBER_RENEW;
            memberExpiredDate = memberCard.getMemberCardValidTime().plusDays(durationDays);
            userMemberCardService.lambdaUpdate()
                    .set(UserMemberCard::getMemberCardValidTime, memberExpiredDate)
                    .eq(BaseEntity::getId, memberCard.getId())
                    .update();
        }
        UserAccount user = userAccountService.lambdaQuery()
                .select(UserAccount::getUserNickname, UserAccount::getUserPhone, UserAccount::getUserHeadPortrait)
                .eq(UserAccount::getUserId, userId)
                .one();
        //更新用户最后交易时间
        userAccountService.lambdaUpdate()
                .set(UserAccount::getLastDealTime, LocalDateTime.now())
                .eq(UserAccount::getUserId, userId)
                .update();
        //记录会员流水
        MemberPurchaseHistory history = new MemberPurchaseHistory();
        String memberPurchaseNo = UserConstant.MEMBER_PURCHASE_HISTORY_NO_PREFIX + IdUtil.getSnowflakeNextIdStr();
        history.setNo(memberPurchaseNo)
                .setUserId(userId)
                .setUserNickName(user.getUserNickname())
                .setUserPhone(user.getUserPhone())
                .setMemberId(paidMemberDeal.getPaidMemberId())
                .setEffectiveDurationType(paidMemberDeal.getPaidRuleJson().getEffectiveDurationType())
                .setRankCode(paidMemberDeal.getRankCode())
                .setPayAmount(paidMemberDeal.getPayAmount())
                .setPayType(paidMemberDeal.getPayType())
                .setOrderNo(businessParams.getOrderNum())
                .setType(TransactionType.PAID_MEMBER_OPEN == transactionType ? MemberPurchaseType.PAID_MEMBER_OPEN : MemberPurchaseType.PAID_MEMBER_RENEW)
                .setExpireTime(memberExpiredDate.atTime(LocalTime.MAX))
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());

        memberPurchaseHistoryService.save(history);
        //只有用用户储值购买会员才记录储值流水
        if (PayType.BALANCE.equals(paidMemberDeal.getPayType())) {
            //保存会员储值流水信息
            BalanceChangeDTO balanceChangeDTO = new BalanceChangeDTO();
            DataChangeMessage dataChangeMessage = new DataChangeMessage();
            dataChangeMessage.setUserId(userId);
            dataChangeMessage.setChangeType(ChangeType.REDUCE);
            dataChangeMessage.setOperatorType(TransactionType.PAID_MEMBER_OPEN.equals(transactionType) ?
                    BalanceHistoryOperatorType.PURCHASE_MEMBERSHIP : BalanceHistoryOperatorType.RENEWAL_MEMBERSHIP);
            dataChangeMessage.setValue(paidMemberDeal.getPayAmount());
            dataChangeMessage.setOrderNo(history.getNo());
            dataChangeMessage.setOperatorUserId(userId);
            balanceChangeDTO.setPersonDataChangeMessage(dataChangeMessage);
            userBalanceHistoryManageService.asyncSaveUserBalanceHistory(balanceChangeDTO, null, null);
        }
        //异步发送 mq 消息
        globalExecutor.execute(
                () -> {
                    // 生成对账单
                    rabbitTemplate.convertAndSend(
                            StatementRabbit.OVERVIEW_STATEMENT.exchange(),
                            StatementRabbit.OVERVIEW_STATEMENT.routingKey(),
                            new OverviewStatementDTO()
                                    .setTransactionSerialNumber(transactionId)
                                    .setUserNickname(user.getUserNickname())
                                    .setUserAvatar(user.getUserHeadPortrait())
                                    .setAccount(paidMemberDeal.getPayAmount())
                                    .setOrderNo(memberPurchaseNo)
                                    .setUserId(userId)
                                    .setShopId(SecurityConst.NO_SHOP_ID_CLIENT_DEFAULT_SHOP_ID)
                                    .setTransactionTime(LocalDateTime.now())
                                    .setTransactionType(transactionType)
                                    .setChangeType(ChangeType.INCREASE));
                    if (paidMemberDeal.getPayType() != PayType.WECHAT) {
                        return;
                    }
                    // 延迟队列 小程序发货 立即发可能会出现支付单不存在
                    rabbitTemplate.convertAndSend(
                            UserRabbit.USER_VIRTUAL_DELIVER.exchange(),
                            UserRabbit.USER_VIRTUAL_DELIVER.routingKey(),
                            new UserVirtualDeliverDTO()
                                    .setTransactionId(transactionId)
                                    .setOpenId(businessParams.getOpenId())
                                    .setDesc(transactionType == TransactionType.PAID_MEMBER_RENEW ? "付费会员续费" : "付费会员开通")
                                    .setPlatform(businessParams.getPayPlatform()),
                            message -> {
                                //延迟俩分钟
                                message.getMessageProperties().setHeader(MessageProperties.X_DELAY, 2 * 60 * 1000);
                                return message;
                            });
                }
        );
    }


    @Override
    public Option<UserMemberCard> checkFreeMemberCardInfo(UserAccount user) {
        return Option.of(
                userFreeMemberService.lambdaQuery()
                        .le(UserFreeMember::getNeedValue, user.getGrowthValue())
                        .orderByDesc(UserFreeMember::getRankCode)
                        .last(SqlHelper.SQL_LIMIT_1)
                        .one()
        ).map(
                userFreeMember -> new UserMemberCard()
                        .setMemberName(userFreeMember.getFreeMemberName())
                        .setRankCode(userFreeMember.getRankCode())
                        .setMemberCardStatus(MemberCardStatus.NORMAL)
                        .setMemberCardValidTime(LocalDate.now())
                        .setUserId(user.getUserId())
                        .setMemberId(userFreeMember.getId())
                        .setOpenType(OpenType.GROWTH_VALUE_REACH)
                        .setMemberType(MemberType.FREE_MEMBER)
        );
    }

    @Override
    public Map<Long, Boolean> getUserRights(Set<Long> userIds, RightsType rightsType) {
        //初始数据默认是所有用户都没有这项权益
        Map<Long, Boolean> userRightsMap = userIds.stream()
                .collect(Collectors.toMap(
                        Function.identity(), userId -> false
                ));
        // 查询权益类型是否开启
        MemberRights activeRights = memberRightsService.lambdaQuery()
                .eq(MemberRights::getRightsType, rightsType)
                .eq(MemberRights::getRightsSwitch, Boolean.TRUE)
                .one();
        if (activeRights == null) {
            // 权益未开启，所有用户都无该权益
            return userRightsMap;
        }

        Long activeRightsId = activeRights.getId();

        // 获取用户中是付费会员的用户ids
        List<UserMemberCard> paidMemberCards = userMemberCardService.lambdaQuery()
                .eq(UserMemberCard::getMemberCardStatus, MemberCardStatus.NORMAL)
//                .groupBy(UserMemberCard::getUserId)
                .in(UserMemberCard::getUserId, userIds)
                .list();
        Set<Long> paidMemberUserIds = paidMemberCards.stream().map(UserMemberCard::getUserId).collect(Collectors.toSet());

        // 排除付费会员 ids   拿到免费会员信息
        Set<Long> freeMemberUserIds = new HashSet<>(userIds);
        freeMemberUserIds.removeAll(paidMemberUserIds);
        if (rightsType == RightsType.EXCLUSIVE_SERVICE) {
            /*
             *   1. 检查用户是否是付费会员
             *   2.付费会员是否包含该权益
             *   3.免费会员是否包含该权益
             */

            if (!freeMemberUserIds.isEmpty()) {

                List<RankedMemberVO> rankedFreeMembers = TenantShop.disable(() -> {
                    return userAccountService.getRankedMember(freeMemberUserIds);

                });
                Set<Long> freeMemberRankIds = rankedFreeMembers.stream()
                        .map(RankedMemberVO::getFreeMemberId)
                        .collect(Collectors.toSet());

                Map<Long, Long> collect = rankedFreeMembers.stream().collect(Collectors.toMap(RankedMemberVO::getUserId, RankedMemberVO::getFreeMemberId));

                // 获取用户当前会员级别是否有相关权益
                Set<Long> memberIdsWithRights = Optional.ofNullable(userMemberRelevancyRightsService.lambdaQuery()
                                .in(UserMemberRelevancyRights::getMemberId, freeMemberRankIds)
                                .eq(UserMemberRelevancyRights::getMemberRightsId, activeRightsId)
                                .list())
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(UserMemberRelevancyRights::getMemberId)
                        .collect(Collectors.toSet());

                freeMemberUserIds.forEach(userId -> userRightsMap.put(userId, memberIdsWithRights.contains(collect.get(userId))));
            }
            //付费会员
            if (CollUtil.isNotEmpty(paidMemberCards)) {
                paidMemberCards = paidMemberCards.stream()
                        .collect(Collectors.toMap(
                                UserMemberCard::getUserId,
                                user -> user,
                                (u1, u2) -> u1.getRankCode() > u2.getRankCode() ? u1 : u2
                        ))
                        .values()
                        .stream()
                        .collect(Collectors.toList());
                List<Long> paidMemberIds = userAddonSupporter.queryHasRightsMemberId(activeRightsId);
                paidMemberCards.forEach(paidMemberCard -> {
                    Long memberId = paidMemberCard.getMemberId();
                    if (paidMemberIds.contains(memberId)) {
                        userRightsMap.put(paidMemberCard.getUserId(), true);
                    }
                });

            }

        }

        return userRightsMap;
    }

    @Override
    public boolean queryMemberInUse(Long memberId) {
        Long count = userMemberCardService.lambdaQuery()
                .eq(UserMemberCard::getMemberId, memberId)
                .eq(UserMemberCard::getDeleted, Boolean.FALSE)
                .eq(UserMemberCard::getMemberCardStatus, MemberCardStatus.NORMAL)
                .gt(UserMemberCard::getMemberCardValidTime, LocalDate.now())
                .count();
        return count > 0;
    }

    /**
     * 免费会员聚合数据组装
     *
     * @param user 用户信息
     * @return 会员信息
     */
    private MemberAggregationInfoVO freeMemberAggregationAssemble(UserAccount user) {
        MemberAggregationInfoVO memberInfo = new MemberAggregationInfoVO()
                .setUserNickname(user.getUserNickname())
                .setUserHeadPortrait(user.getUserHeadPortrait())
                .setGrowthValue(user.getGrowthValue())
                .setMemberType(MemberType.FREE_MEMBER);

        return checkFreeMemberCardInfo(user)
                .map(
                        (userMemberCard) -> {
                            //免费会员权益
                            List<RelevancyRightsVO> relevancyRights = userFreeMemberService.getRelevancyRights(userMemberCard.getMemberId());
                            Map<RightsType, List<RelevancyRightsVO>> rightMap = Maps.newHashMap();
                            if (CollUtil.isNotEmpty(relevancyRights)) {
                                rightMap = relevancyRights.stream().collect(Collectors.groupingBy(RelevancyRightsVO::getRightsType));
                            }
                            return memberInfo.setCurrentMemberVO(
                                    new CurrentMemberVO()
                                            .setMemberCardId(userMemberCard.getId())
                                            .setRankCode(userMemberCard.getRankCode() == null ? CommonPool.NUMBER_ZERO : userMemberCard.getRankCode())
                                            .setMemberName(userMemberCard.getMemberName())
                                            .setRelevancyRights(rightMap)
                            );
                        }
                ).getOrElse(() -> memberInfo.setCurrentMemberVO(new CurrentMemberVO().setRankCode(CommonPool.NUMBER_ZERO)));

    }

    /**
     * 处理用户到期会员卡
     *
     * @param paidMemberInfo 付费会员卡信息
     * @param userId         用户id
     */
    private void disposeExpireMemberCard(PaidMemberInfoVO paidMemberInfo, Long userId) {
        CompletableTask.allOf(globalExecutor,
                () -> {
                    //更新付费会员卡过期数据
                    if (CollUtil.isNotEmpty(paidMemberInfo.getMemberCloseIds())) {
                        userMemberCardService.lambdaUpdate()
                                .set(UserMemberCard::getMemberCardStatus, MemberCardStatus.ABNORMAL)
                                .in(UserMemberCard::getMemberId, paidMemberInfo.getMemberCloseIds())
                                .eq(UserMemberCard::getUserId, userId)
                                .update();
                    }
                },
                () -> {
                    // 更新会员正常到期的用户会员卡
                    userMemberCardService.lambdaUpdate()
                            .set(UserMemberCard::getMemberCardStatus, MemberCardStatus.EXPIRE)
                            .lt(UserMemberCard::getMemberCardValidTime, LocalDate.now())
                            .eq(UserMemberCard::getUserId, userId)
                            .eq(UserMemberCard::getMemberCardStatus, MemberCardStatus.NORMAL)
                            .update();
                }
        );
    }

}