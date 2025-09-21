package com.medusa.gruul.user.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.BalanceHistoryOperatorType;
import com.medusa.gruul.common.model.enums.ChangeType;
import com.medusa.gruul.common.model.enums.StatementRabbit;
import com.medusa.gruul.common.model.enums.TransactionType;
import com.medusa.gruul.common.model.message.DataChangeMessage;
import com.medusa.gruul.common.model.message.OverviewStatementDTO;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.IManualTransaction;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.global.model.helper.RangeMap;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.payment.api.enums.DealType;
import com.medusa.gruul.payment.api.model.dto.PayNotifyResultDTO;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.service.uaa.api.dto.UserAuthorityDTO;
import com.medusa.gruul.service.uaa.api.dto.UserRegisterDataDTO;
import com.medusa.gruul.service.uaa.api.enums.Gender;
import com.medusa.gruul.user.api.constant.UserConstant;
import com.medusa.gruul.user.api.enums.*;
import com.medusa.gruul.user.api.model.dto.BalanceChangeDTO;
import com.medusa.gruul.user.api.model.dto.UserDataDTO;
import com.medusa.gruul.user.api.model.vo.PaidMemberInfoVO;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import com.medusa.gruul.user.api.model.vo.UserPersonVo;
import com.medusa.gruul.user.service.excel.ExcelExportUtil;
import com.medusa.gruul.user.service.model.dto.*;
import com.medusa.gruul.user.service.model.vo.UserExportVO;
import com.medusa.gruul.user.service.model.vo.UserListVO;
import com.medusa.gruul.user.service.model.vo.UserTagMapVO;
import com.medusa.gruul.user.service.mp.entity.*;
import com.medusa.gruul.user.service.mp.mapper.UserAccountMapper;
import com.medusa.gruul.user.service.mp.service.*;
import com.medusa.gruul.user.service.mq.Sender;
import com.medusa.gruul.user.service.service.IUserBalanceHistoryManageService;
import com.medusa.gruul.user.service.service.MemberCardService;
import com.medusa.gruul.user.service.service.UserGrowthValueService;
import com.medusa.gruul.user.service.service.UserManageService;
import com.medusa.gruul.user.service.service.addon.UserAddonSupporter;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import io.vavr.control.Option;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author WuDi
 * date: 2022/9/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserManageServiceImpl implements UserManageService {

    private final Sender sender;
    private final Executor globalExecutor;
    private final RabbitTemplate rabbitTemplate;
    private final IUserAccountService userAccountService;
    private final IUserFreeMemberService userFreeMemberService;
    private final UserAccountMapper userAccountMapper;
    private final IUserCollectService userCollectService;
    private final IUserFootMarkService userFootMarkService;
    private final MemberCardService memberCardService;
    private final IShopUserAccountService shopUserAccountService;
    private final UserGrowthValueService userGrowthValueService;
    private final IUserMemberCardService userMemberCardService;
    private final UserAddonSupporter userAddonSupporter;
    private final SqlSessionFactory sqlSessionFactory;
    private final IUserBalanceHistoryManageService userBalanceHistoryManageService;


    private GoodsRpcService goodsRpcService;

    @Lazy
    @Autowired
    public void setGoodsRpcService(GoodsRpcService goodsRpcService) {
        this.goodsRpcService = goodsRpcService;
    }


    /**
     * 编辑会员信息
     *
     * @param userData 用户信息
     */
    @Override
    @Redisson(value = UserConstant.USER_DATA_UPDATE_LOCK, key = "#userData.userId")
    public void updateUser(UserDataDTO userData) {
        Long userId = userData.getUserId();
        boolean exists = userAccountService.lambdaQuery()
                .eq(UserAccount::getUserId, userId)
                .exists();
        String genderName = userData.getGender();
        Gender gender = StrUtil.isEmpty(genderName) ? null : Gender.valueOf(genderName);
        if (exists) {
            // 更新会员昵称，头像
            userAccountService.lambdaUpdate()
                    .set(UserAccount::getUserNickname, userData.getNickname())
                    .set(UserAccount::getUserHeadPortrait, userData.getAvatar())
                    .set(UserAccount::getGender, gender)
                    .set(userData.getBirthday() != null, UserAccount::getBirthday, userData.getBirthday())
                    .set(userData.getUsername() != null, UserAccount::getUserName, userData.getUsername())
                    .eq(UserAccount::getUserId, userId)
                    .update();
            shopUserAccountService.lambdaUpdate()
                    .set(ShopUserAccount::getUserNickname, userData.getNickname())
                    .set(ShopUserAccount::getUserHeadPortrait, userData.getAvatar())
                    .eq(ShopUserAccount::getUserId, userId)
                    .update();

            return;
        }
        // 新增会员信息
        userAccountService.save(
                new UserAccount()
                        .setUserId(userId)
                        .setUserNickname(userData.getNickname())
                        .setUserHeadPortrait(userData.getAvatar())
                        .setGender(gender)
                        .setUserPhone(userData.getMobile())
                        .setBalance(0L)
                        .setConsumeCount(0)
                        .setDealTotalMoney(0L)
                        .setDistributionCount(0)
                        .setIntegralTotal(0L)
                        .setBirthday(Option.of(userData.getBirthday()).getOrNull())
                        .setUserName(userData.getUsername())
                        .setGrowthPayOrderCount(0)
                        .setGrowthPayOrderMoney(0L)
                        .setGrowthValue(userGrowthValueService.getUserGrowthValueSettings() != null && userGrowthValueService.getUserGrowthValueSettings().getRegisterEnabled() ? userGrowthValueService.getUserGrowthValueSettings().getRegisterGrowthValue() : 0L)
        );
    }


    @Override
    @Redisson(value = UserConstant.USER_DATA_UPDATE_LOCK, key = "#balanceChangeMessage.userId")
    public void userBalanceChange(DataChangeMessage balanceChangeMessage) {
        Long userId = balanceChangeMessage.getUserId();
        UserAccount user = userAccountService.lambdaQuery().eq(UserAccount::getUserId, userId).one();
        if (user == null) {
            throw new GlobalException("当前用户信息不存在");
        }
        Long userBalance = user.getBalance();
        userBalance = userBalance == null ? 0L : userBalance;

        Long changeAmount = balanceChangeMessage.getValue();
        changeAmount = ChangeType.INCREASE == balanceChangeMessage.getChangeType() ? changeAmount : -(changeAmount > userBalance ? userBalance : changeAmount);

        userAccountService.lambdaUpdate()
                .set(UserAccount::getBalance, userBalance + changeAmount)
                .set(UserAccount::getLastDealTime, LocalDateTime.now())
                .eq(UserAccount::getUserId, userId)
                .update();

        balanceChangeMessage.setValue(Math.abs(changeAmount));
        balanceChangeMessage.setExtendInfo(DealType.SYSTEM_CHARGING.name());
        balanceChangeMessage.setOperatorType(ChangeType.INCREASE == balanceChangeMessage.getChangeType() ?
                BalanceHistoryOperatorType.SYSTEM_RECHARGE : BalanceHistoryOperatorType.SYSTEM_DEDUCTION);
        balanceChangeMessage.setOperatorUserId(ISecurity.userMust().getId());
        BalanceChangeDTO balanceChange = new BalanceChangeDTO();
        balanceChange.setUserNikeName(user.getUserNickname());
        balanceChange.setUserHeadPortrait(user.getUserHeadPortrait());
        balanceChange.setSystemDataChangeMessage(balanceChangeMessage);
        // mq通知余额生成明细记录
        sender.sendPaymentChangeMessage(balanceChange);
        //用户储值流水
        userBalanceHistoryManageService.asyncSaveUserBalanceHistory(balanceChange, null, null);
    }

    /**
     * 会员余额充值
     * todo 优化 1。幂等消费 。。。
     *
     * @param payNotifyResultDTO PayNotifyResultDTO.java
     */
    @Override
    @Redisson(value = UserConstant.USER_DATA_UPDATE_LOCK, key = "#payNotifyResultDTO.businessParams.userId")
    public void userBalanceRecharge(PayNotifyResultDTO payNotifyResultDTO) {
        BalanceChangeDTO balanceChange = new BalanceChangeDTO();
        PaymentInfoDTO businessParams = payNotifyResultDTO.getBusinessParams();
        MemberBalanceDealDTO memberBalanceDeal = JSON.parseObject(businessParams.getAttach(), MemberBalanceDealDTO.class);
        UserAccount user = userAccountService.lambdaQuery().eq(UserAccount::getUserId, businessParams.getUserId()).one();
        //本次充值用户合计获取金额
        Long totalGainBalance = memberBalanceDeal.getPayAmount() + memberBalanceDeal.getRuleJson().getPresentedMoney();
        user.setBalance(user.getBalance() + totalGainBalance);
        //用户储值充值更新最后交易时间
        user.setLastDealTime(LocalDateTime.now());
        user.setGrowthValue(user.getGrowthValue() + memberBalanceDeal.getRuleJson().getPresentedGrowthValue());
        userAccountService.updateById(user);
        shopUserAccountService.lambdaUpdate()
                .set(ShopUserAccount::getBalance, user.getBalance())
                .set(ShopUserAccount::getGrowthValue, user.getGrowthValue())
                .eq(ShopUserAccount::getUserId, businessParams.getUserId())
                .update();
        LocalDateTime now = LocalDateTime.now();
        String transactionId = payNotifyResultDTO.getShopIdTransactionMap().values().iterator().next().getTransactionId();
        Long systemSeqNo = IdUtil.getSnowflakeNextId();
        Long personSeqNo = IdUtil.getSnowflakeNextId();
        CompletableTask.allOf(globalExecutor,
                () -> {
                    //封装数据
                    DataChangeMessage dataChangeMessage = new DataChangeMessage();
                    dataChangeMessage.setChangeType(ChangeType.INCREASE);
                    dataChangeMessage.setUserId(businessParams.getUserId());
                    dataChangeMessage.setValue(memberBalanceDeal.getPayAmount());
                    dataChangeMessage.setExtendInfo(DealType.PERSONAL_CHARGING.name());
                    dataChangeMessage.setOperatorUserId(user.getUserId());
                    dataChangeMessage.setOperatorType(BalanceHistoryOperatorType.USER_RECHARGE);
                    // mq通知余额生成明细记录
                    if (memberBalanceDeal.getRuleJson().getPresentedMoney() > Long.valueOf(CommonPool.NUMBER_ZERO)) {
                        log.debug("赠送余额");
                        DataChangeMessage systemDataChangeMessage = new DataChangeMessage();
                        systemDataChangeMessage.setValue(memberBalanceDeal.getRuleJson().getPresentedMoney());
                        systemDataChangeMessage.setExtendInfo(DealType.SYSTEM_GIVE.name());
                        systemDataChangeMessage.setChangeType(ChangeType.INCREASE);
                        systemDataChangeMessage.setUserId(user.getUserId());
                        //系统赠送 填充操作用户id为0
                        systemDataChangeMessage.setOperatorUserId((long) CommonPool.NUMBER_ZERO);
                        systemDataChangeMessage.setOperatorType(BalanceHistoryOperatorType.SYSTEM_GIFT);
                        balanceChange.setSystemDataChangeMessage(systemDataChangeMessage);
                    }
                    balanceChange.setPersonDataChangeMessage(dataChangeMessage);
                    balanceChange.setPayType(businessParams.getPayType());
                    balanceChange.setUserHeadPortrait(user.getUserHeadPortrait());
                    balanceChange.setUserNikeName(user.getUserNickname());
                    log.warn("发送余额变动信息mq");
                    userBalanceHistoryManageService.asyncSaveUserBalanceHistory(balanceChange, systemSeqNo, personSeqNo);
                    sender.sendPaymentChangeMessage(balanceChange);

                },
                () -> {
                    // 生成对账单
                    rabbitTemplate.convertAndSend(
                            StatementRabbit.OVERVIEW_STATEMENT.exchange(),
                            StatementRabbit.OVERVIEW_STATEMENT.routingKey(),
                            new OverviewStatementDTO()
                                    .setTransactionSerialNumber(transactionId)
                                    .setUserNickname(user.getUserNickname())
                                    .setUserAvatar(user.getUserHeadPortrait())
                                    .setUserId(user.getUserId())
                                    .setOrderNo(String.valueOf(personSeqNo))
                                    .setShopId((long) CommonPool.NUMBER_ZERO)
                                    .setTransactionType(TransactionType.USER_BALANCE_RECHARGE)
                                    .setAccount(memberBalanceDeal.getPayAmount())
                                    .setChangeType(ChangeType.INCREASE)
                                    .setTransactionTime(now)
                    );
                    // 赠送金额mq
                    if (memberBalanceDeal.getRuleJson().getPresentedMoney() > 0) {
                        // 生成对账单
                        rabbitTemplate.convertAndSend(
                                StatementRabbit.OVERVIEW_STATEMENT.exchange(),
                                StatementRabbit.OVERVIEW_STATEMENT.routingKey(),
                                new OverviewStatementDTO()
                                        .setTransactionSerialNumber(transactionId)
                                        .setUserNickname(user.getUserNickname())
                                        .setUserAvatar(user.getUserHeadPortrait())
                                        .setOrderNo(String.valueOf(systemSeqNo))
                                        .setUserId(user.getUserId())
                                        .setShopId((long) CommonPool.NUMBER_ZERO)
                                        .setTransactionType(TransactionType.RECHARGE_GIFT)
                                        .setAccount(memberBalanceDeal.getRuleJson().getPresentedMoney())
                                        .setChangeType(ChangeType.REDUCE)
                                        .setTransactionTime(now)
                        );
                    }
                },
                // 延迟队列 小程序发货 立即发可能会出现支付单不存在
                () -> rabbitTemplate.convertAndSend(
                        UserRabbit.USER_BALANCE_VIRTUAL_DELIVER.exchange(),
                        UserRabbit.USER_BALANCE_VIRTUAL_DELIVER.routingKey(),
                        new UserVirtualDeliverDTO()
                                .setTransactionId(transactionId)
                                .setOpenId(businessParams.getOpenId())
                                .setDesc("余额储值")
                                .setPlatform(businessParams.getPayPlatform()),
                        message -> {
                            //延迟俩分钟
                            message.getMessageProperties().setHeader(MessageProperties.X_DELAY, 2 * 60 * 1000);
                            return message;
                        })
        );


    }

    @Override
    public Long getUserBalance() {
        Long userId = ISecurity.userMust().getId();
        UserAccount user = userAccountService.lambdaQuery().eq(UserAccount::getUserId, userId).one();
        if (user == null) {
            throw new GlobalException("当前用户不存在");
        }
        return user.getBalance();
    }

    @Override
    public Long getTodayUvNum() {
        Long count = userAccountService.lambdaQuery()
                .like(BaseEntity::getCreateTime, LocalDate.now() + "%").count();
        return count == null ? CommonPool.NUMBER_ZERO : count;
    }

    /**
     * 订单完成更新用户交易总额
     *
     * @param orderCompleted 订单完成
     */
    @Override
    public void updateUserTradeTotalAmount(OrderCompletedDTO orderCompleted) {
        Long totalAmount = orderCompleted.getTotalAmount();
        if (totalAmount == 0) {
            return;
        }
        Long userId = orderCompleted.getUserId();
        UserAccount user = userAccountService.lambdaQuery()
                .select(UserAccount::getDealTotalMoney, UserAccount::getGrowthValue, UserAccount::getGrowthPayOrderMoney, UserAccount::getGrowthPayOrderCount)
                .eq(UserAccount::getUserId, userId)
                .one();
        if (user == null) {
            throw new GlobalException("当前用户不存在");
        }
        Tuple3<Long, Boolean, Boolean> tuple3 = getGrowthValue(orderCompleted, user);
        boolean update;
        if (tuple3 != null) {
            // 是否增加成长值
            boolean addGrowth = tuple3._1 > 0;
            // 是否增加订单数量
            Boolean isGrowthPayOrderCount = tuple3._2;

            update = userAccountService.lambdaUpdate()
                    //消费总金额
                    .setSql(StrUtil.format(UserConstant.USER_DEAL_TOTAL_MONEY_SQL_TEMPLATE, totalAmount))
                    //通过订单数量方式才会更新订单数量 如果达到订单目标则重置订单数量
                    .setSql(isGrowthPayOrderCount, tuple3._3 ? UserConstant.USER_GROWTH_PAY_ORDER_COUNT_SQL_RESET_TEMPLATE :
                            StrUtil.format(UserConstant.USER_GROWTH_PAY_ORDER_COUNT_SQL_TEMPLATE, 1))
                    //奖励成长值的实付金额
                    .setSql(StrUtil.format(UserConstant.USER_PAY_ORDER_MONEY_SQL_TEMPLATE, totalAmount))
                    //增加的成长值
                    .setSql(addGrowth, StrUtil.format(UserConstant.USER_GROWTH_VALUE_SQL_TEMPLATE, tuple3._1))
                    .eq(UserAccount::getUserId, userId)
                    .update();
        } else {
            update = userAccountService.lambdaUpdate()
                    .set(UserAccount::getDealTotalMoney, user.getDealTotalMoney() + totalAmount)
                    .eq(UserAccount::getUserId, userId)
                    .update();
        }
        if (!update) {
            throw new GlobalException("更新用户交易总额失败");
        }

    }


    private Tuple3<Long, Boolean, Boolean> getGrowthValue(OrderCompletedDTO orderCompleted, UserAccount userAccount) {
        UserGrowthValueSettings userGrowthValueSettings = userGrowthValueService.getUserGrowthValueSettings();
        if (userGrowthValueSettings == null) {
            return null;
        }
        ConsumeJsonDTO consumeJson = userGrowthValueSettings.getConsumeJson()
                .stream()
                .filter(ConsumeJsonDTO::getIsSelected)
                .findFirst()
                .orElse(null);
        if (consumeJson == null) {
            return null;
        }
        long growthValue;
        Long orderQuantityAndAmount = consumeJson.getOrderQuantityAndAmount();
        ConsumeGrowthValueType consumeGrowthValueType = consumeJson.getConsumeGrowthValueType();
        //是否重置 用于统计订单数完成后重置的标记
        Boolean reset = Boolean.FALSE;
        if (consumeJson.getConsumeGrowthValueType() == ConsumeGrowthValueType.ORDER_QUANTITY) {
            //订单数量
            //是否达到设置的订单数标准 +1 是把当前的这笔订单算上
            boolean reached = ObjectUtil.defaultIfNull(userAccount.getGrowthPayOrderCount() + 1, 0) >= orderQuantityAndAmount;
            if (reached) {
                growthValue = consumeJson.getPresentedGrowthValue();
                reset = Boolean.TRUE;
            } else {
                growthValue = 0;
            }
        } else {
            //订单金额计算方式
            BigDecimal tempNum = BigDecimal.valueOf(orderCompleted.getTotalAmount())
                    .divide(BigDecimal.valueOf(CommonPool.UNIT_CONVERSION_TEN_THOUSAND))
                    .divide(BigDecimal.valueOf(consumeJson.getOrderQuantityAndAmount()), RoundingMode.DOWN);
            growthValue = tempNum.longValue() > 0 ? tempNum.longValue() * consumeJson.getPresentedGrowthValue() : 0L;
        }
        if (growthValue > 0) {
            //成长值叠加会员的权益
            Map<RightsType, List<RelevancyRightsVO>> relevancyRights = memberCardService.getMemberCentre(orderCompleted.getUserId()).getCurrentMemberVO().getRelevancyRights();
            List<RelevancyRightsVO> relevancyRightsVOS = relevancyRights.get(RightsType.GROWTH_VALUE_MULTIPLE);
            if (CollUtil.isNotEmpty(relevancyRights) && relevancyRightsVOS != null) {
                growthValue = relevancyRightsVOS.get(0).getExtendValue() > 0 ?
                        growthValue + growthValue * relevancyRightsVOS.get(0).getExtendValue() : growthValue;
            }
        }
        return Tuple.of(growthValue, consumeGrowthValueType == ConsumeGrowthValueType.ORDER_QUANTITY, reset);
    }

    /**
     * 获取用户详细信息
     *
     * @param userId 用户id
     */
    @Override
    public UserListVO getUserParticulars(Long userId) {
        UserListVO userParticulars = userAccountMapper.getUserParticulars(userId);

        if (userParticulars == null) {
            return null;
        }
        if (userParticulars.getMember() == null) {
            userParticulars.setMember(
                    new UserListVO.MemberVO()
                            .setMemberType(MemberType.FREE_MEMBER)
                            .setRankCode(
                                    Option.of(userFreeMemberService.lambdaQuery()
                                                    .select(UserFreeMember::getRankCode)
                                                    .le(UserFreeMember::getNeedValue, userParticulars.getGrowthValue())
                                                    .orderByDesc(UserFreeMember::getRankCode)
                                                    .last(CommonPool.SQL_LIMIT_1)
                                                    .one()
                                            )
                                            .map(UserFreeMember::getRankCode)
                                            .getOrElse(CommonPool.NUMBER_ONE)
                            )
            );
        }
        return userParticulars;

    }


    /**
     * 用户会员列表数据
     *
     * @param userQuery 会员列表查询参数
     * @return 用户会员列表VO
     */
    @Override
    public IPage<UserListVO> getUserList(UserQueryDTO userQuery) {
        //分页查询用户
        IPage<UserListVO> userPage = userAccountMapper.userPage(userQuery);
        List<UserListVO> users = userPage.getRecords();
        if (CollUtil.isEmpty(users)) {
            return userPage;
        }
        //所有的用户id
        Set<Long> userIds = users.stream().map(UserListVO::getUserId).collect(Collectors.toSet());
        //用户标签
        Map<Long, List<UserTagMapVO>> userIdTagsMap = CollUtil.emptyIfNull(userAccountMapper.getTagsMapByUserIds(userIds))
                .stream()
                .collect(Collectors.groupingBy(UserTagMapVO::getUserId));
        //有付费会员则使用付费会员等级，否则使用免费会员 并设置用户标签
        users.forEach(user ->
                user.setUserTagVOList(CollUtil.emptyIfNull(userIdTagsMap.get(user.getUserId())).stream().map(tagMap -> new UserListVO.TagVO().setTagId(tagMap.getTagId()).setTagName(tagMap.getTagName())).toList())
        );
        return userPage;
    }

    /**
     * 用户权益编辑
     *
     * @param userAuthority 用户权益dto
     */
    @Override
    public void editMemberAuthority(UserAuthorityDTO userAuthority) {
        boolean flag = userAuthority.getRoles().contains(Roles.USER);
        Set<Long> userIds = userAuthority.getUserIds();
        if (flag) {
            //清除用户权限
            this.userAccountService.lambdaUpdate()
                    .set(UserAccount::getUserAuthority, null)
                    .set(UserAccount::getExplain, null)
                    .in(UserAccount::getUserId, userIds)
                    .update();
            return;
        }

        String explain = userAuthority.getExplain();
        //没有填写说明 且只有一个用户 则使用该用户的原始说明
        boolean notEmptyExplain = StrUtil.isNotBlank(explain);
        if (notEmptyExplain) {
            this.userAccountService.lambdaUpdate()
                    .set(UserAccount::getUserAuthority, JSON.toJSONString(userAuthority.getRoles()))
                    .set(UserAccount::getExplain, explain)
                    .in(UserAccount::getUserId, userIds)
                    .update();
        }
        //说明为空 使用批量查询的用户原始说明 进行批量更新
        Map<Long, String> explainMap = userAccountService.lambdaQuery()
                .select(UserAccount::getUserId, UserAccount::getExplain)
                .in(UserAccount::getUserId, userIds)
                .list()
                .stream()
                .collect(
                        Collectors.toMap(UserAccount::getUserId, UserAccount::getExplain)
                );
        //手动开启一个事物批量更新
        IManualTransaction.todo(
                () -> SqlHelper.executeBatch(
                        sqlSessionFactory,
                        new Slf4jImpl(this.getClass().getSimpleName()),
                        (sqlSession) -> {
                            UserAccountMapper mapper = sqlSession.getMapper(UserAccountMapper.class);
                            for (Long userId : userIds) {
                                mapper.update(
                                        Wrappers.lambdaUpdate(UserAccount.class)
                                                .set(UserAccount::getUserAuthority, JSON.toJSONString(userAuthority.getRoles()))
                                                .set(UserAccount::getExplain, explainMap.get(userId))
                                                .eq(UserAccount::getUserId, userId)
                                );
                            }

                        }
                )
        );
    }


    /**
     * 用户黑名单列表查询
     *
     * @param userBlacklistQuery 用户黑名单查询dto
     * @return IPage<UserListVO>
     */
    @Override
    public IPage<UserListVO> getUserBlacklist(UserBlacklistQueryDTO userBlacklistQuery) {
        IPage<UserListVO> userPage = userAccountMapper.getUserBlacklist(userBlacklistQuery);
        List<UserListVO> users = userPage.getRecords();
        if (CollUtil.isEmpty(users)) {
            return userPage;
        }
        Set<Long> userIds = users.stream().map(UserListVO::getUserId).collect(Collectors.toSet());
        //付费会员
        Map<Long, Integer> userIdRankCodeMap = userAccountMapper.memberCardByUserId(userIds).stream().collect(Collectors.toMap(UserMemberCard::getUserId, UserMemberCard::getRankCode));
        //有付费会员则使用付费会员等级，否则使用免费会员 并设置用户标签
        List<UserListVO> freeMemberUser = users.stream()
                .filter(
                        user -> Option.of(userIdRankCodeMap.get(user.getUserId()))
                                .peek(rankCode -> user.setMember(new UserListVO.MemberVO().setMemberType(MemberType.PAID_MEMBER).setRankCode(rankCode)))
                                .isEmpty()
                ).toList();
        renderFreeMember(freeMemberUser);
        return userPage;
    }

    private void renderFreeMember(List<UserListVO> freeMemberUser) {
        if (CollUtil.isEmpty(freeMemberUser)) {
            return;
        }
        List<UserFreeMember> freeMembers = userFreeMemberService.lambdaQuery()
                .select(UserFreeMember::getRankCode, UserFreeMember::getNeedValue)
                .list();
        UserListVO.MemberVO defaultMember = new UserListVO.MemberVO()
                .setMemberType(MemberType.FREE_MEMBER)
                .setRankCode(0);
        if (CollUtil.isEmpty(freeMembers)) {
            freeMemberUser.forEach(user -> user.setMember(defaultMember));
            return;
        }
        //使用区间map 查询对应成长值对应的免费会员等级
        RangeMap<Long, Integer> rangeMap = new RangeMap<>();
        rangeMap.putAll(freeMembers, UserFreeMember::getNeedValue, UserFreeMember::getRankCode);
        freeMemberUser.forEach(
                user -> {
                    Long growthValue = user.getGrowthValue();
                    user.setMember(
                            new UserListVO.MemberVO()
                                    .setMemberType(MemberType.FREE_MEMBER)
                                    .setRankCode(rangeMap.optGet(growthValue == null ? 0L : growthValue).getOrElse(0))
                    );
                }
        );
    }

    /**
     * 用户个人中心数据
     *
     * @param userId userId
     * @return 用户个人中心数据
     */
    @Override
    public UserPersonVo userPerson(Long userId) {
        UserPersonVo userPersonVo = new UserPersonVo();
        userPersonVo.setCollectCount(getUserCollectInfoCount(userId));
        userPersonVo.setFootprint(getUserFootprint(userId));
        getUserIntegralAndBalance(userId, userPersonVo);
        return userPersonVo;
    }


    /**
     * 处理用户足迹
     *
     * @param userId userId
     */
    private Long getUserFootprint(Long userId) {
        return userFootMarkService.lambdaQuery().eq(UserFootMark::getUserId, userId).count();
    }

    /**
     * 获取用户积分
     *
     * @param userId userId
     */
    private void getUserIntegralAndBalance(Long userId, UserPersonVo userPersonVo) {
        UserAccount userAccount = Option.of(userAccountService.lambdaQuery().eq(UserAccount::getUserId, userId).one())
                .getOrElseThrow(() -> {
                    throw new GlobalException(SystemCode.DATA_NOT_EXIST_CODE, "用户数据不存在");
                });
        userPersonVo.setIntegral(userAccount.getIntegralTotal()).setBalance(userAccount.getBalance());
    }

    /**
     * 获取用户收藏
     *
     * @param userId userid
     * @return 收藏数量
     */
    private Long getUserCollectInfoCount(Long userId) {
        Long goodsCollectCount = userCollectService.lambdaQuery()
                .eq(UserCollect::getUserId, userId)
                .count();
        return goodsCollectCount + goodsRpcService.shopFollow(userId);
    }

    /**
     * 批量注册用户
     *
     * @param userDataList 用户注册信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userBatchRegister(List<UserRegisterDataDTO> userDataList) {
        Set<Integer> paidRankCode = new HashSet<>(CommonPool.NUMBER_TEN);
        Set<Integer> freeRankCode = new HashSet<>(CommonPool.NUMBER_TEN);
        List<UserRegisterDataDTO> paidFreeUserData = new ArrayList<>();
        for (UserRegisterDataDTO userRegister : userDataList) {
            switch (userRegister.getMemberType()) {
                case PAID_MEMBER -> {
                    paidRankCode.add(userRegister.getRankCode());
                    paidFreeUserData.add(userRegister);
                }
                case FREE_MEMBER -> freeRankCode.add(userRegister.getRankCode());
            }
        }
        // 保存会员卡信息
        saveUserMemberCard(paidRankCode, paidFreeUserData);
        Map<Integer, Long> freeMemberMap = Collections.emptyMap();
        if (CollUtil.isNotEmpty(freeRankCode)) {
            freeMemberMap = userFreeMemberService.lambdaQuery()
                    .in(UserFreeMember::getRankCode, freeRankCode)
                    .list().stream()
                    .collect(Collectors.toMap(UserFreeMember::getRankCode, UserFreeMember::getNeedValue));
        }
        Map<Integer, Long> finalFreeMemberMap = freeMemberMap;
        // 新增会员信息
        userAccountService.saveBatch(
                userDataList.stream()
                        .map(userData -> new UserAccount()
                                .setUserId(userData.getUserId())
                                .setUserNickname(userData.getNickname())
                                .setUserHeadPortrait(userData.getAvatar())
                                .setUserPhone(userData.getMobile())
                                .setGender(userData.getGender())
                                .setBalance(userData.getBalance() == null ? 0L :
                                        userData.getBalance().multiply(BigDecimal.valueOf(CommonPool.UNIT_CONVERSION_TEN_THOUSAND)).longValue())

                                .setConsumeCount(0)
                                .setDealTotalMoney(0L)
                                .setDistributionCount(0)
                                .setIntegralTotal(userData.getIntegralTotal())
                                .setBirthday(userData.getBirthday())
                                .setUserName(userData.getUserName())
                                .setGrowthPayOrderCount(0)
                                .setGrowthPayOrderMoney(0L)
                                .setGrowthValue(userData.getMemberType() == MemberType.FREE_MEMBER ? Optional.ofNullable(finalFreeMemberMap.get(userData.getRankCode())).orElse(0L) : 0L)
                        ).toList()
        );
    }

    @Override
    public void exportUser(HttpServletResponse response, UserQueryDTO userQuery) {
        userQuery.setSize(userQuery.getTotal());
        IPage<UserListVO> userPage = userAccountMapper.userPage(userQuery);
        List<UserListVO> records = userPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            exportExcel(response, new ArrayList<>());
            return;
        }
        BigDecimal tenThousand = BigDecimal.valueOf(CommonPool.UNIT_CONVERSION_TEN_THOUSAND);
        List<UserExportVO> userExports = records.stream()
                .map(user ->
                        {
                            UserExportVO userExportVO = new UserExportVO();
                            userExportVO.setUserName(user.getUserName() == null ? user.getUserNickname() : user.getUserName());
                            userExportVO.setUserPhone(user.getUserPhone());
                            userExportVO.setBirthday(user.getBirthday());
                            userExportVO.setGender(user.getGender().getDesc());
                            userExportVO.setMemberType(user.getMemberType().getDesc());
                            userExportVO.setRankCode(user.getRankCode());
                            userExportVO.setGrowthValue(user.getGrowthValue());
                            userExportVO.setBalance(BigDecimal.valueOf(user.getBalance()).divide(tenThousand, 2, RoundingMode.HALF_EVEN));
                            userExportVO.setDealTotalMoney(BigDecimal.valueOf(user.getDealTotalMoney()).divide(tenThousand, 2, RoundingMode.HALF_EVEN));
                            userExportVO.setIntegralTotal(user.getIntegralTotal());
                            return userExportVO;
                        }
                ).toList();
        exportExcel(response, userExports);

    }

    @Override
    public void growthValueBalanceChange(UserGrowthValueDTO userGrowthValue) {
        Long userId = userGrowthValue.getUserId();
        UserAccount userAccount = userAccountService.lambdaQuery()
                .select(UserAccount::getGrowthValue)
                .eq(UserAccount::getUserId, userId)
                .one();
        if (userAccount == null) {
            return;
        }
        Long growthValue = userGrowthValue.getGrowthValue();
        ChangeType changeType = userGrowthValue.getChangeType();
        growthValue = changeType == ChangeType.REDUCE ? -(growthValue > userAccount.getGrowthValue() ? userAccount.getGrowthValue() : growthValue) : growthValue;
        userAccountService.lambdaUpdate()
                .setSql(StrUtil.format(UserConstant.USER_GROWTH_VALUE_SQL_TEMPLATE, growthValue))
                .eq(UserAccount::getUserId, userId)
                .update();
        shopUserAccountService.lambdaUpdate()
                .setSql(StrUtil.format(UserConstant.USER_GROWTH_VALUE_SQL_TEMPLATE, growthValue))
                .eq(ShopUserAccount::getUserId, userId)
                .update();


    }


    protected void exportExcel(HttpServletResponse response, List<UserExportVO> data) {
        try {
            ExcelExportUtil.exportExcel(response, "模板-批量导出用户", "模板-批量导出用户", data, UserExportVO.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveUserMemberCard(Set<Integer> paidRankCode, List<UserRegisterDataDTO> paidFreeUserData) {
        if (CollectionUtils.isEmpty(paidRankCode)) {
            return;
        }
        Map<Integer, PaidMemberInfoVO> paidMemberMap = userAddonSupporter.getPaidMembers(MemberType.PAID_MEMBER);
        if (CollectionUtils.isEmpty(paidMemberMap) || !paidMemberMap.keySet().containsAll(paidRankCode)) {
            return;
        }
        LocalDate now = LocalDate.now();
        List<UserMemberCard> memberCards = paidFreeUserData.stream()
                .filter(userRegister -> paidMemberMap.containsKey(userRegister.getRankCode()))
                .map(paidMember -> {
                    PaidMemberInfoVO paidMemberInfo = paidMemberMap.get(paidMember.getRankCode());
                    return new UserMemberCard()
                            .setUserId(paidMember.getUserId())
                            .setMemberType(MemberType.PAID_MEMBER)
                            .setRankCode(paidMemberInfo.getRankCode())
                            .setMemberId(paidMemberInfo.getMemberId())
                            .setMemberName(paidMemberInfo.getPaidMemberName())
                            .setOpenType(OpenType.PAID_BUY)
                            .setMemberCardStatus(MemberCardStatus.NORMAL)
                            .setMemberCardValidTime(now.plusDays(paidMemberInfo.getPaidRuleJson().get(CommonPool.NUMBER_ZERO).getEffectiveDurationType().getValue()));
                }).toList();
        userMemberCardService.saveBatch(memberCards);
    }
}
