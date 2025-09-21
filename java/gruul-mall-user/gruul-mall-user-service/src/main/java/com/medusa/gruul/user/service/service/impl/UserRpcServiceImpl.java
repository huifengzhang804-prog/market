package com.medusa.gruul.user.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.payment.api.enums.PaymentSubjectEnum;
import com.medusa.gruul.user.api.constant.UserConstant;
import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.api.enums.RightsType;
import com.medusa.gruul.user.api.model.UserDataVO;
import com.medusa.gruul.user.api.model.UserFootMarkVO;
import com.medusa.gruul.user.api.model.dto.BalanceChangeDTO;
import com.medusa.gruul.user.api.model.dto.UserDataDTO;
import com.medusa.gruul.user.api.model.dto.UserFootMarkDTO;
import com.medusa.gruul.user.api.model.vo.MemberAggregationInfoVO;
import com.medusa.gruul.user.api.model.vo.PaidMemberInfoVO;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import com.medusa.gruul.user.service.mp.entity.UserAccount;
import com.medusa.gruul.user.service.mp.entity.UserFreeMember;
import com.medusa.gruul.user.service.mp.service.IMemberRightsService;
import com.medusa.gruul.user.service.mp.service.IUserAccountService;
import com.medusa.gruul.user.service.mp.service.IUserFootMarkService;
import com.medusa.gruul.user.service.mp.service.IUserFreeMemberService;
import com.medusa.gruul.user.service.service.*;
import com.medusa.gruul.user.service.service.addon.UserAddonSupporter;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaoq description date 2022-10-12 16:13
 */
@Service
@DubboService
@RequiredArgsConstructor
public class UserRpcServiceImpl implements UserRpcService {

    private final MemberCardService memberCardService;
    private final UserManageService userManageService;
    private final IUserAccountService userAccountService;
    private final IMemberRightsService memberRightsService;
    private final UserAddonSupporter userAddonSupporter;
    private final IUserFootMarkService userFootMarkService;
    private final IUserFreeMemberService userFreeMemberService;
    private final UserCollectManageService userCollectManageService;
    private final UserFootMarkManageService userFootMarkManageService;
    private final IUserBalanceHistoryManageService userBalanceHistoryManageService;


    @Override
    public void updateUser(UserDataDTO userData) {
        userManageService.updateUser(userData);
    }

    @Override
    public UserDataVO userData(Long userId) {
        UserAccount user = userAccountService.lambdaQuery()
                .select(UserAccount::getUserId, UserAccount::getUserNickname, UserAccount::getUserHeadPortrait, UserAccount::getUserPhone)
                .eq(UserAccount::getUserId, userId)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        return new UserDataVO()
                .setUserId(user.getUserId())
                .setNickname(user.getUserNickname())
                .setAvatar(user.getUserHeadPortrait())
                .setMobile(user.getUserPhone());
    }

    /**
     * 用户进行余额支付扣除余额
     *
     * @param balanceChangeMessage 数据变化消息
     * @return 修改结果
     */
    @Override
    @Redisson(value = UserConstant.USER_BALANCE_KEY, key = "#balanceChangeMessage.userId")
    @Transactional(rollbackFor = Exception.class)
    public Boolean userBalancePayment(DataChangeMessage balanceChangeMessage) {
        UserAccount userInfo = userAccountService.lambdaQuery()
                .eq(UserAccount::getUserId, balanceChangeMessage.getUserId()).one();
        BalanceChangeDTO balanceChange = new BalanceChangeDTO();
        balanceChange.setPersonDataChangeMessage(balanceChangeMessage);
        switch (balanceChangeMessage.getChangeType()) {
            case REDUCE -> {
                //.扣余额
                if (userInfo.getBalance() >= balanceChangeMessage.getValue()) {
                    //扣款后的金额
                    long afterAmount = userInfo.getBalance() - balanceChangeMessage.getValue();
                    balanceChangeMessage.setAfterValue(afterAmount);
                    userInfo.setBalance(afterAmount);
                    boolean flag = userAccountService.updateById(userInfo);
                    if (Objects.nonNull(balanceChangeMessage.getSubject()) &&
                            !PaymentSubjectEnum.USER_OPENS_PAID_MEMBERSHIP.getSubject()
                                    .equals(balanceChangeMessage.getSubject())) {
                        //非会员充值才保存流水记录 会员充值 从购物消费中分离出来 已经做了记录
                        userBalanceHistoryManageService.asyncSaveUserBalanceHistory(balanceChange, null, null);
                    }
                    return flag;
                }
                return Boolean.FALSE;
            }
            case INCREASE -> {
                // 加余额
                long afterAmount = userInfo.getBalance() + balanceChangeMessage.getValue();
                userInfo.setBalance(afterAmount);
                balanceChangeMessage.setAfterValue(afterAmount);
                boolean flag = userAccountService.updateById(userInfo);
                userBalanceHistoryManageService.asyncSaveUserBalanceHistory(balanceChange, null, null);
                return flag;
            }
            default -> throw new GlobalException("未知改变条件");
        }
    }

    @Override
    public Long getUserConsumption(Long userId) {
        return Option.of(userAccountService.lambdaQuery().select(UserAccount::getDealTotalMoney)
                        .eq(UserAccount::getUserId, userId).one())
                .map(UserAccount::getDealTotalMoney)
                .getOrElse(0L);
    }

    @Override
    public Map<Long, Long> getUserConsumptionBatch(Set<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return Map.of();
        }
        List<UserAccount> accounts = userAccountService.lambdaQuery()
                .select(UserAccount::getUserId, UserAccount::getDealTotalMoney)
                .in(UserAccount::getUserId, userIds)
                .list();
        Map<Long, Long> userIdConsumptionMap = accounts.stream()
                .collect(
                        Collectors.toMap(
                                UserAccount::getUserId,
                                UserAccount::getDealTotalMoney
                        )
                );
        return userIds.stream()
                .collect(
                        Collectors.toMap(userId -> userId, userId -> userIdConsumptionMap.getOrDefault(userId, 0L))
                );
    }

    /**
     * 查看店铺的商品收藏量
     *
     * @param shopId 店铺id
     * @return 商品和收藏量Map
     */
    @Override
    public List<Long> getShopProductCollection(Long shopId) {
        return userCollectManageService.getShopProductCollection(shopId);
    }

    /**
     * 获取付费会员关联权益信息
     *
     * @param map map <付费会员id,Set<Long> 关联权益id>
     * @return Map<Long, List < RelevancyRightsVO>>
     */
    @Override
    public Map<Long, List<RelevancyRightsVO>> getPaidMemberRelevancyInfo(Map<Long, Set<Long>> map) {
        Map<Long, List<RelevancyRightsVO>> relevancyRightsMap = new HashMap<>(CommonPool.NUMBER_EIGHT);
        map = Collections.synchronizedMap(map);
        for (Long paidMemberId : map.keySet()) {
            //获取会员关联权益数据
            Set<Long> ids = map.get(paidMemberId);
            List<RelevancyRightsVO> relevancyRightsList = new ArrayList<>();
            if (ids.size() > CommonPool.NUMBER_ZERO) {
                //代表付费会员关联了权益
                relevancyRightsList = memberRightsService.getRightByIds(ids);
            }
            relevancyRightsMap.put(paidMemberId, relevancyRightsList);
        }
        return relevancyRightsMap;
    }

    /**
     * 获取最高等级会员卡数据
     */
    @Override
    public MemberAggregationInfoVO getTopMemberCardInfo(Long userId) {
        return memberCardService.getMemberCentre(userId);
    }

    @Override
    public Long getUserIntegralTotal(Long userId) {

        return Option.of(
                        this.userAccountService.lambdaQuery()
                                .select(UserAccount::getIntegralTotal)
                                .eq(UserAccount::getUserId, userId)
                                .one()
                )
                .map(UserAccount::getIntegralTotal)
                .getOrElse(0L);

    }


    @Override
    @Redisson(value = UserConstant.USER_INTEGRAL_TOTAL_KEY, key = "#userId")
    public boolean incrementUserIntegralTotal(Long userId, Long integral, boolean isReduce) {

        if (isReduce) {
            //减少积分
            return this.userAccountService.lambdaUpdate()
                    .eq(UserAccount::getUserId, userId)
                    .ge(UserAccount::getIntegralTotal, integral)
                    //更新积分
                    .setSql(
                            StrUtil.format(UserConstant.USER_ACCOUNT_INTEGRAL_TOTAL_SQL_TEMPLATE, -integral)
                    )
                    .update();
        }

        //增加积分
        return this.userAccountService.lambdaUpdate()
                .eq(UserAccount::getUserId, userId)
                //更新积分
                .setSql(
                        StrUtil.format(UserConstant.USER_ACCOUNT_INTEGRAL_TOTAL_SQL_TEMPLATE, integral)
                )
                .update();
    }

    /**
     * 获取免费会员等级code
     *
     * @return 会员等级
     */
    @Override
    public Integer getFreeMemberRankCode() {
        UserFreeMember userFreeMember = userFreeMemberService.query()
                .select("MAX(rank_code) AS rank_code")
                .one();
        return userFreeMember != null ? userFreeMember.getRankCode() : CommonPool.NUMBER_ZERO;
    }

    /**
     * 获取所有会员等级
     *
     * @return 会员等级
     */
    @Override
    public Map<MemberType, Map<Integer, String>> getAllMemberRankCode() {
        List<UserFreeMember> userFreeMembers = userFreeMemberService.lambdaQuery()
                .select(UserFreeMember::getFreeMemberName, UserFreeMember::getRankCode)
                .groupBy(UserFreeMember::getRankCode)
                .orderByAsc(UserFreeMember::getRankCode)
                .list();
        Map<Integer, PaidMemberInfoVO> paidMembers = userAddonSupporter.getPaidMembers(MemberType.PAID_MEMBER);
        Map<MemberType, Map<Integer, String>> map = new HashMap<>(CommonPool.NUMBER_TWO);
        if (CollUtil.isNotEmpty(userFreeMembers)) {
            map.put(MemberType.FREE_MEMBER, userFreeMembers.stream()
                    .collect(Collectors.toMap(UserFreeMember::getRankCode, UserFreeMember::getFreeMemberName)));
        }
        if (CollUtil.isEmpty(paidMembers)) {
            return map;
        }
        map.put(MemberType.PAID_MEMBER, paidMembers.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getPaidMemberName())));
        return map;
    }

    /**
     * 获取店铺浏览量
     *
     * @param shopIds 店铺id数组
     * @return 店铺id，浏览量
     */
    @Override
    public Map<Long, Long> getFootMarkCount(Set<Long> shopIds) {
        List<UserFootMarkVO> footMarkList = TenantShop.disable(() -> userFootMarkService.getFootMarkCount(shopIds));
        return footMarkList.stream().collect(
                Collectors.toMap(UserFootMarkVO::getShopId, UserFootMarkVO::getTotal)
        );
    }

    @Override
    public Map<Long, String> queryUserPhone(Set<Long> userIds) {

        return TenantShop.disable(
                () -> userAccountService.queryUserPhoneInfo(userIds));
    }

    /**
     * 当前用户是否关注店铺商品
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 是否关注：true-关注
     */
    @Override
    public Boolean findUserIsCollect(Long shopId, Long productId, Long userId) {
        return userCollectManageService.findUserIsCollect(shopId, productId, userId);
    }

    @Override
    public boolean isUserCollectAndAddFootMark(Long userId, UserFootMarkDTO footMark) {
        //添加用户足迹
        userFootMarkManageService.addUserFootMark(footMark, userId);
        //查询用户是否已收藏该商品
        return userCollectManageService.findUserIsCollect(footMark.getShopId(), footMark.getProductId(), userId);
    }

    /**
     * 获取用户权限Map，包含每个用户的权限信息
     *
     * @param userIds   用户ID集合，需要检查这些用户的权限
     * @param rightsType 权限类型，指定需要检查的权限类型
     * @return Map，其中键是用户ID，值是一个布尔值，表示该用户是否具有指定的权限
     */
    @Override
    public Map<Long, Boolean> getUserRights(Set<Long> userIds, RightsType rightsType) {
        return memberCardService.getUserRights(userIds,rightsType);
    }

    /**
     * 查询会员等级是否在使用中，即用户是否有购买该等级的会员 并且没有到期
     * @param memberId
     * @return
     */
    @Override
    public boolean queryMemberInUse(Long memberId) {

        return memberCardService.queryMemberInUse(memberId);
    }

    @Override
    public Map<Long, UserDataVO> queryUserBaseInfo(Set<Long> userIds) {
        return userAccountService.queryBaseInfo(userIds);
    }

}
