package com.medusa.gruul.addon.rebate.service.imipl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.medusa.gruul.addon.rebate.model.RebateConstant;
import com.medusa.gruul.addon.rebate.model.bo.UserRebatePercent;
import com.medusa.gruul.addon.rebate.model.dto.WithdrawDTO;
import com.medusa.gruul.addon.rebate.model.enums.RebateError;
import com.medusa.gruul.addon.rebate.model.enums.RebateType;
import com.medusa.gruul.addon.rebate.model.vo.RebatePayDTO;
import com.medusa.gruul.addon.rebate.model.vo.RebateTransactionsVO;
import com.medusa.gruul.addon.rebate.mp.entity.*;
import com.medusa.gruul.addon.rebate.mp.service.IRebateDetailsService;
import com.medusa.gruul.addon.rebate.mp.service.IRebatePaymentService;
import com.medusa.gruul.addon.rebate.mp.service.IRebateTransactionsService;
import com.medusa.gruul.addon.rebate.service.RebateConfHandleService;
import com.medusa.gruul.addon.rebate.service.RebateTransactionsHandleService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.goods.api.model.dto.EstimateRebateDTO;
import com.medusa.gruul.overview.api.enums.DrawType;
import com.medusa.gruul.overview.api.enums.OverviewRabbit;
import com.medusa.gruul.overview.api.enums.OwnerType;
import com.medusa.gruul.overview.api.enums.WithdrawSourceType;
import com.medusa.gruul.overview.api.model.DrawTypeModel;
import com.medusa.gruul.overview.api.model.WithdrawOrderDTO;
import com.medusa.gruul.overview.api.rpc.OverviewRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 返利余额操作实现类
 *
 * @author jinbu
 */
@Service
@RequiredArgsConstructor
public class RebateTransactionsHandleServiceImpl implements RebateTransactionsHandleService {

    private final RabbitTemplate rabbitTemplate;
    private final OverviewRpcService overviewRpcService;
    private final IRebateDetailsService rebateDetailsService;
    private final IRebatePaymentService rebatePaymentService;
    private final RebateConfHandleService rebateConfHandleService;
    private final IRebateTransactionsService rebateTransactionsService;
    private final UaaRpcService uaaRpcService;

    @Override
    public Long getProductRebateAmount(Long userId, EstimateRebateDTO estimate) {
        //查询返利配置
        Option<RebateConf> configOpt = rebateConfHandleService.getConfigOpt();
        long zero = CommonPool.NUMBER_ZERO;
        RebateConf rebateConf;
        if (configOpt.isEmpty() || (rebateConf = configOpt.get()).disabled()) {
            return zero;
        }
        //计算返利真实比例
        BigDecimal rebateRate = Option.of(userId)
                .map(id -> {
                    UserRebatePercent userRebatePercent = rebateConfHandleService.getUserRebatePercent(id);
                    return userRebatePercent == null ? BigDecimal.ZERO : userRebatePercent.rebateRate();
                }).getOrElse(
                        () -> AmountCalculateHelper.getDiscountRate(
                                rebateConf.vipRebateConfigs()
                                        .stream()
                                        .map(RebateUsersExtendValue::getRebatePercentage)
                                        .max(Long::compareTo)
                                        .orElse(zero),
                                CommonPool.UNIT_CONVERSION_TEN_THOUSAND
                        )
                );

        //服务费的抵扣金额
        Long serviceFeeDiscountAmount =
                SellType.CONSIGNMENT == estimate.getSellType() && estimate.getSkuSalePrice() != null
                        ? estimate.getSkuSalePrice() : zero;
        //计算服务费的基础金额
        Long serviceFeeBaseAmount = estimate.getAmount() - serviceFeeDiscountAmount;
        //优先计算服务费
        return AmountCalculateHelper.getDiscountAmount(
                AmountCalculateHelper.getDiscountAmount(serviceFeeBaseAmount, estimate.serviceFeeRate()),
                rebateRate
        );
    }


    /**
     * 获取可支付消费返利余额
     *
     * @param payAmount 支付金额
     * @return 可支付消费返利余额
     */
    @Override
    public RebatePayDTO getCanRebateBalance(String orderNo, Long payAmount) {
        Long userId = ISecurity.userMust().getId();
        RebatePayDTO result = new RebatePayDTO()
                .setDisabled(Boolean.TRUE);
        //查询是否支付过 支付过 不能再支付
        if (rebatePaymentService.lambdaQuery()
                .eq(RebatePayment::getOrderNo, orderNo)
                .exists()) {
            return result;
        }
        //获取当前用户获取返利的比率 和返利支付比率
        UserRebatePercent userRebatePercent = rebateConfHandleService.getUserRebatePercent(userId);
        //如果返利未开启 不能支付
        if (userRebatePercent.isDisabled()) {
            return result;
        }
        result.setDisabled(Boolean.FALSE);
        //查询用户返利余额
        RebateTransactions rebateTransactions = rebateTransactionsService.lambdaQuery()
                .select(RebateTransactions::getRebateBalance)
                .eq(RebateTransactions::getUserId, userId)
                .one();
        return result.setBalance(
                // 计算返利的金额
                // 1.无余额 -> 0
                // 2.有余额 支付金额*返利支付百分比 和 余额 取最小值
                rebateTransactions == null || rebateTransactions.getRebateBalance() <= 0 ? 0L :
                        Math.min(
                                AmountCalculateHelper.getDiscountAmount(payAmount, userRebatePercent.payRate()),
                                rebateTransactions.getRebateBalance()
                        )
        );
    }

    /**
     * 获取用户消费返利相关余额
     *
     * @return 用户消费返利余额
     */
    @Override
    public RebateTransactionsVO getRebateTransactions() {
        Long userId = ISecurity.userMust().getId();
        RebateTransactions rebateTransactions = rebateTransactionsService.lambdaQuery()
                .eq(RebateTransactions::getUserId, userId)
                .one();
        RebateTransactionsVO rebateTransactionsVO = new RebateTransactionsVO();
        if (rebateTransactions == null) {
            return rebateTransactionsVO;
        }
        return rebateTransactionsVO
                .setUserId(userId)
                .setRebateBalance(rebateTransactions.getRebateBalance())
                .setAccumulatedRebate(rebateTransactions.getAccumulatedRebate())
                .setUnsettledRebate(rebateTransactions.getUnsettledRebate())
                .setExpiredRebate(rebateTransactions.getExpiredRebate());


    }

    @Override
    public void updateRebateBalance(RebateTransactions balance, RebateType rebateType, String desc) {
        Long userId = balance.getUserId();
        Long rebateBalance = balance.getRebateBalance();
        RebateTransactions userBalance = rebateTransactionsService.lambdaQuery()
                .eq(RebateTransactions::getUserId, userId)
                .one();
        if (userBalance == null) {
            rebateTransactionsService.save(balance.init());
        } else {
            LambdaUpdateChainWrapper<RebateTransactions> updateWrapper = rebateTransactionsService.lambdaUpdate();
            if (rebateBalance != null) {
                updateWrapper.setSql(StrUtil.format(RebateConstant.REBATE_BALANCE_SQL_TEMPLATE, rebateBalance));
            }
            if (balance.getAccumulatedRebate() != null) {
                updateWrapper.setSql(
                        StrUtil.format(RebateConstant.ACCUMULATED_REBATE_SQL_TEMPLATE, balance.getAccumulatedRebate()));
            }
            if (balance.getUnsettledRebate() != null) {
                updateWrapper.setSql(
                        StrUtil.format(RebateConstant.UNSETTLED_REBATE_SQL_TEMPLATE, balance.getUnsettledRebate()));
            }
            if (balance.getExpiredRebate() != null) {
                updateWrapper.setSql(
                        StrUtil.format(RebateConstant.EXPIRED_REBATE_SQL_TEMPLATE, balance.getExpiredRebate()));
            }
            updateWrapper.eq(RebateTransactions::getUserId, userId)
                    .update();
        }
        if (rebateBalance == null || rebateBalance.equals(0L)) {
            return;
        }
        //生成返利明细
        rebateDetailsService.save(
                new RebateDetails()
                        .setRebateType(rebateType)
                        .setRebateName(desc)
                        .setUserId(userId)
                        .setAmount(Math.abs(rebateBalance))
                        .setChangeType(rebateType.getChangeType())
        );
    }


    /**
     * 消费返利提现
     *
     * @param withdraw 提现方式
     */
    @Override
    @Redisson(value = RebateConstant.REBATE_BALANCE_LOCK_KEY, key = "#secureUser.id")
    public void createWithdrawOrder(SecureUser<?> secureUser, WithdrawDTO withdraw) {
        Long userId = secureUser.getId();
        UserRebatePercent userRebatePercent = rebateConfHandleService.getUserRebatePercent(userId);
        if (userRebatePercent.isDisabled()) {
            throw RebateError.REBATE_DISABLED.exception();
        }
        Long amount = withdraw.getAmount();
        //校验返利提现门槛
        if (amount < userRebatePercent.getWithdrawalThreshold()) {
            throw RebateError.WITHDRAW_AMOUNT_NOT_REACH_MINIMUM_THRESHOLD.dataEx(userRebatePercent.getWithdrawalThreshold());
        }
        //校验返利余额
        RebateTransactions rebateTransactions = rebateTransactionsService.lambdaQuery()
                .eq(RebateTransactions::getUserId, userId)
                .one();
        if (rebateTransactions == null || amount > rebateTransactions.getRebateBalance()) {
            throw RebateError.REBATE_BALANCE_INSUFFICIENT.exception();
        }
        //校验账号信息
        DrawType type = withdraw.getType();
        //获取提现账号
        JSONObject account = withdrawAccount(type, secureUser);
        // 兼容单体 json
        account = account.containsKey("raw") ? account.getJSONObject("raw") : account;
        DrawTypeModel model = new DrawTypeModel().setAmount(amount);

        if (type == DrawType.ALIPAY) {
            //兼容支付宝账户的名称 数据库中存放的是bank 而提取的时候用的是alipayAccount
            if (account.containsKey("bank")) {
                account.set("alipayAccount", account.get("bank"));
            }
        }
        DrawTypeModel dataTypeModel = type.getModelFunction().apply(model, account);

        //更新余额
        this.updateRebateBalance(
                new RebateTransactions()
                        .setUserId(userId)
                        .setRebateBalance(-amount),
                RebateType.WITHDRAW, "消费返利提现"
        );
        //发送提现订单
        UserInfoVO userInfoVO = uaaRpcService.getUserDataByUserId(userId).get();
        rabbitTemplate.convertAndSend(
                OverviewRabbit.WITHDRAW_ORDER_CREATE.exchange(),
                OverviewRabbit.WITHDRAW_ORDER_CREATE.routingKey(),
                new WithdrawOrderDTO()
                        .setApplyUserId(userId)
                        .setApplyUserPhone(ISecurity.userMust().getMobile())
                        .setSourceType(WithdrawSourceType.CONSUMPTION_REBATE)
                        .setOwnerType(OwnerType.REBATE)
                        .setOwnerId(userId)
                        .setName(userInfoVO.getNickname())
                        .setAvatar(userInfoVO.getAvatar())
                        .setContact(secureUser.getMobile())
                        .setDrawType(
                                dataTypeModel
                        )
        );

    }

    @Override
    public Boolean enabled() {
        return rebateConfHandleService.getConfigOpt()
                .map(conf -> !conf.disabled())
                .getOrElse(Boolean.FALSE);
    }

    private JSONObject withdrawAccount(DrawType type, SecureUser<?> secureUser) {
        if (type == DrawType.WECHAT) {
            String openid = secureUser.getOpenid();
            if (StrUtil.isEmpty(openid)) {
                throw RebateError.WECHAT_NOT_BOUND.exception();
            }
            return new JSONObject().set("openid", openid);
        }
        Map<DrawType, JSONObject> accounts = overviewRpcService.getAccountsByUserId(secureUser.getId());
        JSONObject account = accounts.get(type);
        if (account == null) {
            throw RebateError.WITHDRAW_ACCOUNT_NOT_EXISTED.exception();
        }
        return account;
    }

}
