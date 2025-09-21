package com.medusa.gruul.addon.rebate.service.imipl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.addon.rebate.model.RebateConstant;
import com.medusa.gruul.addon.rebate.model.RebateErrorCode;
import com.medusa.gruul.addon.rebate.model.bo.UserRebatePercent;
import com.medusa.gruul.addon.rebate.model.dto.RebateConfDTO;
import com.medusa.gruul.addon.rebate.model.enums.RebateUsers;
import com.medusa.gruul.addon.rebate.mp.entity.RebateConf;
import com.medusa.gruul.addon.rebate.mp.entity.RebateUsersExtendValue;
import com.medusa.gruul.addon.rebate.mp.service.IRebateConfService;
import com.medusa.gruul.addon.rebate.properties.RebateConfigurationProperties;
import com.medusa.gruul.addon.rebate.service.RebateConfHandleService;
import com.medusa.gruul.addon.rebate.util.RebateUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.api.model.vo.MemberAggregationInfoVO;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jinbu
 */
@Service
@RequiredArgsConstructor
public class RebateConfHandleServiceImpl implements RebateConfHandleService {

    private static final Long MAX_WITHDRAWAL_THRESHOLD = 1000000000L;
    private final IRebateConfService rebateConfService;
    private final RebateConfigurationProperties rebateConfigurationProperties;
    private final UserRpcService userRpcService;

    /**
     * 查询消费返利配置
     *
     * @return 消费返利配置
     */
    @Override
    public RebateConf config() {
        RebateConf conf = getConfigOpt().getOrElse(
                () -> new RebateConf()
                        .setRebateStatus(Boolean.FALSE)
                        .setRebateUsers(RebateUsers.PAID_MEMBER)
                        .setPayRebateUsers(List.of())
                        .setAllRebateUsers(List.of())
        );
        //根据会员类型和会员等级分组
        Map<String, RebateUsersExtendValue> allVips = conf.vipRebateConfigs().stream()
                .collect(Collectors.toMap(vip -> RedisUtil.key(vip.getMemberType(), vip.getRankCode()), v -> v));
        //获取当前会员配置
        Map<MemberType, Map<Integer, String>> allMemberRankCode = MapUtil.emptyIfNull(userRpcService.getAllMemberRankCode());
        //合并返利配置与会员配置
        List<RebateUsersExtendValue> mergedPaidVip = MapUtil.emptyIfNull(allMemberRankCode.get(MemberType.PAID_MEMBER))
                .entrySet()
                .stream()
                .map(rankCode -> {
                    RebateUsersExtendValue value = allVips.get(RedisUtil.key(MemberType.PAID_MEMBER, rankCode.getKey()));
                    if (value == null) {
                        value = new RebateUsersExtendValue()
                                .setMemberType(MemberType.PAID_MEMBER)
                                .setMemberName(rankCode.getValue())
                                .setRankCode(rankCode.getKey())
                                .setRebatePercentage((long) CommonPool.NUMBER_ZERO)
                                .setRebatePaymentPercentage((long) CommonPool.NUMBER_ZERO)
                                .setWithdrawalThreshold(MAX_WITHDRAWAL_THRESHOLD);
                    }
                    return value.setMemberName(rankCode.getValue());
                })
                .toList();

        List<RebateUsersExtendValue> mergedFreeVip = MapUtil.emptyIfNull(allMemberRankCode.get(MemberType.FREE_MEMBER))
                .entrySet()
                .stream()
                .map(rankCode -> {
                    RebateUsersExtendValue value = allVips.get(RedisUtil.key(MemberType.FREE_MEMBER, rankCode.getKey()));
                    if (value == null) {
                        value = new RebateUsersExtendValue()
                                .setMemberType(MemberType.FREE_MEMBER)
                                .setMemberName(rankCode.getValue())
                                .setRankCode(rankCode.getKey())
                                .setRebatePercentage((long) CommonPool.NUMBER_ZERO)
                                .setRebatePaymentPercentage((long) CommonPool.NUMBER_ZERO)
                                .setWithdrawalThreshold(MAX_WITHDRAWAL_THRESHOLD);
                    }
                    return value.setMemberName(rankCode.getValue());
                })
                .toList();
        List<RebateUsersExtendValue> allRebateUsers = new ArrayList<>(mergedFreeVip);
        allRebateUsers.addAll(mergedPaidVip);
        return conf.setPayRebateUsers(mergedPaidVip).setAllRebateUsers(allRebateUsers);
    }


    @Override
    public RebateConf getConfig() {
        return getConfigOpt().getOrElseThrow(() -> new GlobalException(RebateErrorCode.REBATE_CONFIG_NOT_EXISTS, "消费返利配置不存在"));
    }

    @Override
    public Option<RebateConf> getConfigOpt() {
        return Option.of(
                RedisUtil.getCacheMap(
                        RebateConf.class,
                        () -> rebateConfService.lambdaQuery().one(),
                        Duration.ofSeconds(rebateConfigurationProperties.getCacheExpire().getConfig()),
                        RebateConstant.REBATE_CONFIG_CACHE_KEY
                )
        );
    }

    /**
     * 编辑消费返利配置
     *
     * @param rebateConfDTO 消费返利配置
     */
    @Override
    public void editRebateConf(RebateConfDTO rebateConfDTO) {
        RebateConf rebateConf = new RebateConf();
        rebateConf.setRebateStatus(rebateConfDTO.getRebateStatus());
        if (rebateConfDTO.getRebateStatus()) {
            rebateConfDTO.validParam();
            rebateConf.setRebateUsers(rebateConfDTO.getRebateUsers())
                    .setPayRebateUsers(CollectionUtil.emptyIfNull(rebateConfDTO.getPayRebateUsers())
                            .stream()
                            .peek(rebateUsersExtendValue -> rebateUsersExtendValue.setId(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(new RebateUsersExtendValue()).longValue()))
                            .collect(Collectors.toList()
                            ))
                    .setAllRebateUsers(CollectionUtil.emptyIfNull(rebateConfDTO.getAllRebateUsers())
                            .stream()
                            .peek(rebateUsersExtendValue -> rebateUsersExtendValue.setId(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(new RebateUsersExtendValue()).longValue()))
                            .toList()
                    );
        }

        rebateConf.setId(rebateConfDTO.getId());
        RedisUtil.doubleDeletion(
                () -> rebateConfService.saveOrUpdate(rebateConf),
                RebateConstant.REBATE_CONFIG_CACHE_KEY
        );


    }

    @Override
    public UserRebatePercent getUserRebatePercent(Long userId) {
        Option<RebateConf> rebateCons = getConfigOpt();
        UserRebatePercent result = new UserRebatePercent();
        RebateConf rebateConf;
        //未开启返利支付 （返利支付禁用中）
        if (rebateCons.isEmpty() || (rebateConf = rebateCons.get()).disabled()) {
            return result.setDisabled(true);
        }
        //已开启
        result.setDisabled(false);
        //返利作用的配置未配置
        if ((CollUtil.isEmpty(rebateConf.getAllRebateUsers()) && CollUtil.isEmpty(rebateConf.getPayRebateUsers()))) {
            return result;
        }
        // 获取用户会员卡信息
        MemberAggregationInfoVO vipCard = userRpcService.getTopMemberCardInfo(userId);
        //用户没有会员卡（包含 普通和付费会员） 不能使用返利
        if (vipCard == null) {
            return result;
        }
        //获取返利百分比
        return RebateUtil.rebatePercent(rebateConf, vipCard)
                .setVipCard(vipCard)
                .setDisabled(false);
    }
}
