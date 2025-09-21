package com.medusa.gruul.user.service.service.impl;

import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.payment.api.enums.FeeType;
import com.medusa.gruul.payment.api.model.dto.PaymentInfoDTO;
import com.medusa.gruul.payment.api.model.pay.PayResult;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import com.medusa.gruul.user.api.enums.UserRabbit;
import com.medusa.gruul.user.service.model.dto.MemberBalanceDealDTO;
import com.medusa.gruul.user.service.model.dto.RuleJsonDTO;
import com.medusa.gruul.user.service.model.dto.UserPayDTO;
import com.medusa.gruul.user.service.model.dto.UserSavingRuleDTO;
import com.medusa.gruul.user.service.mp.entity.UserAccount;
import com.medusa.gruul.user.service.mp.entity.UserSavingRule;
import com.medusa.gruul.user.service.mp.service.IUserSavingRuleService;
import com.medusa.gruul.user.service.service.UserSavingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


/**
 * @author xiaoq Description date 2022-09-01 14:04
 */
@Service
@RequiredArgsConstructor
public class UserSavingServiceImpl implements UserSavingService {

  private final IUserSavingRuleService userSavingRuleService;


  private final PaymentRpcService paymentRpcService;

  /**
   * 获取储值信息
   */
  @Override
  public UserSavingRule getSavingManageInfo() {
    UserSavingRule userSavingRule = userSavingRuleService.lambdaQuery()
        .select(BaseEntity::getId,
            UserSavingRule::getSwitching,
            UserSavingRule::getDiscountsState,
            UserSavingRule::getRuleJson)
        .eq(BaseEntity::getDeleted, Boolean.FALSE)
        .one();
    if (userSavingRule == null) {
      userSavingRule = new UserSavingRule();
      userSavingRule.setSwitching(Boolean.FALSE);
      userSavingRule.setDiscountsState(Boolean.FALSE);
      userSavingRuleService.save(userSavingRule);
      userSavingRule.setId(userSavingRule.getId());
      //后端数据处理不返回baseEntity属性数据
      userSavingRule.setVersion(null);
      userSavingRule.setUpdateTime(null);
      userSavingRule.setCreateTime(null);
      userSavingRule.setDeleted(null);
    }
    return userSavingRule;
  }

  /**
   * 修改储值信息状态
   *
   * @param status 储值信息状态
   */
  @Override
  public void setSavingManageSwitch(Boolean status) {
    UserSavingRule userSavingRule = userSavingRuleService.lambdaQuery().one();
    if (userSavingRule == null) {
      throw new GlobalException("当前储值信息不存在");
    }
    userSavingRuleService.lambdaUpdate().set(UserSavingRule::getSwitching, status).update();
  }

  /**
   * 编辑用户储值信息
   */
  @Override
  public void editSavingManageInfo(UserSavingRuleDTO userSavingRule) {
    // peek设置key值
    List<RuleJsonDTO> collect = userSavingRule.getRuleJson()
        .stream()
        .peek(ruleJson -> ruleJson.setId(
            MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(new UserSavingRule()).longValue()))
        .toList();
    boolean update = userSavingRuleService.lambdaUpdate()
        .set(UserSavingRule::getDiscountsState, userSavingRule.getDiscountsState())
        .set(UserSavingRule::getRuleJson, JSON.toJSONString(collect))
        .eq(BaseEntity::getId, userSavingRule.getId())
        .update();
    if (!update) {
      throw new GlobalException("存储规则修改失败");
    }
  }

  /**
   * 用户储值支付
   *
   * @param userPay 用户支付信息
   */
  @Override
  public PayResult userSavingPay(UserPayDTO userPay) {
    MemberBalanceDealDTO memberBalanceDeal = new MemberBalanceDealDTO();
    Long userId = ISecurity.userMust().getId();
    UserSavingRule userSavingRule = userSavingRuleService.lambdaQuery()
        .eq(UserSavingRule::getSwitching, Boolean.TRUE)
        .one();
    if (userSavingRule == null) {
      throw new GlobalException("当前规则不存在,或平台已关闭会员功能");
    }
    RuleJsonDTO ruleJson = new RuleJsonDTO();
    ruleJson.setPresentedGrowthValue(Long.valueOf(CommonPool.NUMBER_ZERO));
    ruleJson.setPresentedMoney(Long.valueOf(CommonPool.NUMBER_ZERO));
    if (userPay.getRuleId() != null) {
      Optional<RuleJsonDTO> ruleJsonOptional = userSavingRule.getRuleJson()
          .stream().filter(bean -> userPay.getRuleId().equals(bean.getId())).findAny();
      ruleJson = ruleJsonOptional.orElseThrow(() -> new GlobalException("当前充值规则不存在"));
      if (!ruleJson.getLadderMoney().equals(userPay.getPayAmount())
          || !userSavingRule.getDiscountsState()) {
        //用户自定义充值 不享受任何优惠
        ruleJson.setPresentedGrowthValue(Long.valueOf(CommonPool.NUMBER_ZERO));
        ruleJson.setPresentedMoney(Long.valueOf(CommonPool.NUMBER_ZERO));
      }
    }
    //封装交易信息
    memberBalanceDeal.setPayAmount(userPay.getPayAmount());
    memberBalanceDeal.setRuleJson(ruleJson);
    memberBalanceDeal.setUserId(userId);
    memberBalanceDeal.setPayType(userPay.getPayType());

      return initPayResult(memberBalanceDeal);

  }

  /**
   * 初始化支付信息
   *
   * @param memberBalanceDeal 会员余额交易信息
   * @return PayResult 支付结果
   */
  private PayResult initPayResult(MemberBalanceDealDTO memberBalanceDeal) {
    PaymentInfoDTO paymentInfo = new PaymentInfoDTO();
    paymentInfo.setOrderNum(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextUUID(new UserAccount()));
    paymentInfo.setTotalFee(memberBalanceDeal.getPayAmount());
    paymentInfo.setPayType(memberBalanceDeal.getPayType());
    paymentInfo.setUserId(memberBalanceDeal.getUserId());
    paymentInfo.setSubject("用户储值余额");
    paymentInfo.setBody("用户储值充值{}".concat(paymentInfo.getOrderNum()));
    //附加所使用规则json
    paymentInfo.setAttach(JSON.toJSONString(memberBalanceDeal));
    paymentInfo.setExchange(UserRabbit.EXCHANGE);
    paymentInfo.setRouteKey(UserRabbit.USER_RECHARGE_BALANCE_OK.routingKey());

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
