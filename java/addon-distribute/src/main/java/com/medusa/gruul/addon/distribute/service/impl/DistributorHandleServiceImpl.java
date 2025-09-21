package com.medusa.gruul.addon.distribute.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.distribute.model.DistributeConstant;
import com.medusa.gruul.addon.distribute.model.DistributeErrorCode;
import com.medusa.gruul.addon.distribute.model.DistributorCondition;
import com.medusa.gruul.addon.distribute.model.dto.ApplyAffairsDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorQueryDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorRankDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorTeamQueryDTO;
import com.medusa.gruul.addon.distribute.model.enums.ConditionType;
import com.medusa.gruul.addon.distribute.model.enums.DistributorIdentity;
import com.medusa.gruul.addon.distribute.model.enums.DistributorStatus;
import com.medusa.gruul.addon.distribute.model.enums.Level;
import com.medusa.gruul.addon.distribute.model.vo.DistributorApplyStatusVO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorAuditVo;
import com.medusa.gruul.addon.distribute.model.vo.DistributorRankPageVO;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeConf;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeShop;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;
import com.medusa.gruul.addon.distribute.mp.service.IDistributeShopService;
import com.medusa.gruul.addon.distribute.mp.service.IDistributorService;
import com.medusa.gruul.addon.distribute.service.DistributeConfHandleService;
import com.medusa.gruul.addon.distribute.service.DistributorHandleService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.base.BaseEntity;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.payment.api.rpc.PaymentRpcService;
import com.medusa.gruul.service.uaa.api.dto.UserBaseDataDTO;
import com.medusa.gruul.service.uaa.api.enums.SmsType;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import io.vavr.control.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author 张治保 date 2022/11/17
 */
@Service
@RequiredArgsConstructor
public class DistributorHandleServiceImpl implements DistributorHandleService {

    private final UaaRpcService uaaRpcService;
    private final UserRpcService userRpcService;
    private final PaymentRpcService paymentRpcService;
    private final IDistributorService distributorService;
    private final IDistributeShopService distributeShopService;
    private final DistributeConfHandleService platformDistributeService;

    @Override
    public Distributor getAffairsInfoByUserId(Long userId) {
        Distributor distributor = distributorService.lambdaQuery()
                .eq(Distributor::getUserId, userId)
                .eq(Distributor::getIdentity, DistributorIdentity.AFFAIRS)
                .one();
        if (distributor == null) {
            throw new GlobalException(DistributeErrorCode.AFFAIRS_NOT_EXISTED, "当前用户不存在分销商信息");
        }
        if (distributor.getOne() != null) {
            distributor.setReferrer(
                    distributorService.lambdaQuery()
                            .select(Distributor::getName)
                            .eq(Distributor::getUserId, distributor.getOne())
                            .oneOpt()
                            .map(Distributor::getName)
                            .orElse(null)
            );
        }
        distributor.setConfig(platformDistributeService.configMust());
        distributor.setStatistics(distributorService.affairsStatistics(userId));
        return distributor;
    }

    @Override
    @Redisson(value = DistributeConstant.DISTRIBUTOR_LOCK_KEY, key = "#userId")
    public Option<String> getCode(Long userId) {
        Distributor distributor = this.getByUserId(userId).getOrNull();
        //已经是分销商
        if (distributor != null && DistributorIdentity.AFFAIRS == distributor.getIdentity()) {
            return Option.of(distributor.getCode());
        }
        //查询配置
        DistributeConf config = platformDistributeService.configMust();
        DistributorCondition condition = config.getCondition();
        //如果需要申请
        if (!condition.getTypes().contains(ConditionType.CONSUMPTION)) {
            return Option.none();
        }
        //可以通过消费额自动成为分销商
        //消费额不足
        if (userRpcService.getUserConsumption(userId) < condition.getRequiredAmount()) {
            return Option.none();
        }
        LocalDateTime now = LocalDateTime.now();
        //不存在
        if (distributor == null) {
            distributor = new Distributor()
                    .setUserId(userId)
                    .setTotal(0L)
                    .setUndrawn(0L)
                    .setUnsettled(0L)
                    .setInvalid(0L)
                    .setApplyTime(now);
        }
        distributor.setCode(RedisUtil.noStr(DistributeConstant.NO))
                .setIdentity(DistributorIdentity.AFFAIRS)
                .setStatus(DistributorStatus.SUCCESS)
                .setAuditor("system")
                .setPassTime(now);
        //手机号为空 或姓名为空 表示用户未注册成为分销商，此时绑定用户手机号 以及昵称等
        if (StrUtil.isEmpty(distributor.getMobile()) || StrUtil.isEmpty(distributor.getName())) {
            UserInfoVO currentUser = uaaRpcService.getUserDataByUserId(userId)
                    .getOrElseThrow(() -> new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "用户信息异常"));
            if (distributorService.lambdaQuery().eq(Distributor::getMobile, distributor.getMobile())
                    .ne(Distributor::getUserId, userId).exists()) {
                throw new GlobalException(DistributeErrorCode.MOBILE_BEAN_USED, "手机号已被使用");
            }
            distributor.setMobile(currentUser.getMobile())
                    .setName(currentUser.getNickname())
                    .setMobile(currentUser.getMobile())
                    .setNickname(currentUser.getNickname())
                    .setAvatar(currentUser.getAvatar());
        }
        boolean success = distributorService.saveOrUpdate(distributor);
        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
        return Option.of(distributor.getCode());
    }

    @Override
    public DistributorApplyStatusVO applyStatus(Long userId) {
        Distributor distributor = distributorService.lambdaQuery()
                .select(Distributor::getId,
                        Distributor::getStatus, Distributor::getCode, Distributor::getName,
                        Distributor::getMobile, Distributor::getApplyTime, Distributor::getPassTime,
                        BaseEntity::getUpdateTime, Distributor::getRejectReason, Distributor::getVisited
                )
                .eq(Distributor::getUserId, userId)
                .one();
        if (distributor == null) {
            getCode(userId);
            distributor = distributorService.lambdaQuery()
                    .select(Distributor::getId,
                            Distributor::getStatus, Distributor::getCode, Distributor::getName,
                            Distributor::getMobile, Distributor::getApplyTime, Distributor::getPassTime,
                            BaseEntity::getUpdateTime, Distributor::getRejectReason, Distributor::getVisited
                    )
                    .eq(Distributor::getUserId, userId)
                    .one();
        }
        //如果仍为空或身份是分销员 则表示未申请过
        DistributorStatus status;
        if (distributor == null || DistributorStatus.NOT_APPLIED == (status = distributor.getStatus())) {
            return new DistributorApplyStatusVO()
                    .setStatus(DistributorStatus.NOT_APPLIED);
        }
        if (!BooleanUtil.isTrue(distributor.getVisited()) && DistributorStatus.SUCCESS.equals(status)) {
            //首次访问
            distributorService.lambdaUpdate()
                    .set(Distributor::getVisited, Boolean.TRUE)
                    .eq(Distributor::getId, distributor.getId())
                    .update();
        }
        return new DistributorApplyStatusVO()
                .setVisited(distributor.getVisited())
                .setStatus(status)
                .setDistributeCode(distributor.getCode())
                .setName(distributor.getName())
                .setMobile(distributor.getMobile())
                .setApplyTime(distributor.getApplyTime())
                .setAuditTime(
                        DistributorStatus.SUCCESS == status ? distributor.getPassTime() : distributor.getUpdateTime())
                .setRejectReason(distributor.getRejectReason());

    }

    @Override
    public Option<Distributor> getByUserId(Long userId) {
        return Option.of(distributorService.lambdaQuery().eq(Distributor::getUserId, userId).one());
    }

    @Override
    public boolean isAffairs(Long userId) {
        return distributorService.lambdaQuery()
                .eq(Distributor::getIdentity, DistributorIdentity.AFFAIRS)
                .eq(Distributor::getStatus, DistributorStatus.SUCCESS)
                .eq(Distributor::getUserId, userId)
                .exists();
    }

    @Override
    @Redisson(value = DistributeConstant.DISTRIBUTOR_LOCK_KEY, key = "#userId")
    public void scanCode(Long userId, String code) {
        //检查当前用户是否已是分销员/商
        Distributor currentDistributor = this.getByUserId(userId).getOrNull();
        if (currentDistributor != null && currentDistributor.getOne() != null) {
            throw new GlobalException(DistributeErrorCode.DISTRIBUTOR_BOUND, "分销商已绑定");
        }
        //查询分销码对应分销商信息
        Distributor distributor = Option.of(distributorService.lambdaQuery().eq(Distributor::getCode, code).one())
                .getOrElseThrow(
                        () -> new GlobalException(DistributeErrorCode.WRONG_CODE_OF_DISTRIBUTOR, "错误的分销码"));
        //分销商与当前用户不能是同一人
        Long distributorUserId = distributor.getUserId();
        if (userId.equals(distributorUserId)) {
            throw new GlobalException(DistributeErrorCode.CANNOT_BIND_OWN_CODE, "不能绑定自己的分销码");
        }
        //分销商不能交叉绑定
        if (StrUtil.equalsAny(String.valueOf(userId),
                String.valueOf(distributor.getOne()),
                String.valueOf(distributor.getTwo()),
                String.valueOf(distributor.getThree()))) {
            throw new GlobalException(DistributeErrorCode.CANNOT_BIND_CROSS, "不能交叉绑定分销码");
        }
        currentDistributor = getDistributor(userId, currentDistributor);
        currentDistributor.setOne(distributorUserId)
                .setTwo(distributor.getOne())
                .setThree(distributor.getTwo());
        //需不需要更新 当前分销商的二级乃至三级分销员
        if (currentDistributor.getIdentity() == DistributorIdentity.AFFAIRS) {
            //更新下面一级分销员的二级分销商
            distributorService.lambdaUpdate()
                    .eq(Distributor::getOne, userId)
                    .isNull(Distributor::getTwo)
                    .set(Distributor::getTwo, distributorUserId)
                    .ne(Distributor::getUserId, distributorUserId)
                    .update();
            //更新下面二级分销员的三级分销商
            distributorService.lambdaUpdate()
                    .eq(Distributor::getTwo, userId)
                    .isNull(Distributor::getThree)
                    .set(Distributor::getThree, distributorUserId)
                    .ne(Distributor::getUserId, distributorUserId)
                    .update();
        }
        //保存用户为分销员
        SystemCode.DATA_UPDATE_FAILED.falseThrow(
                distributorService.saveOrUpdate(currentDistributor)
        );

    }

    private Distributor getDistributor(Long userId, Distributor currentDistributor) {
        if (currentDistributor == null) {
            //远程查询用户资料
            UserInfoVO currentUser = uaaRpcService.getUserDataByUserId(userId)
                    .getOrElseThrow(() -> new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "用户信息异常"));
            currentDistributor = new Distributor()
                    .setUserId(userId)
                    .setIdentity(DistributorIdentity.USER)
                    .setTotal(0L)
                    .setUndrawn(0L)
                    .setUnsettled(0L)
                    .setInvalid(0L)
                    .setStatus(DistributorStatus.NOT_APPLIED)
                    .setNickname(currentUser.getNickname())
                    .setAvatar(currentUser.getAvatar());
        }
        return currentDistributor;
    }

    @Override
    @Redisson(value = DistributeConstant.DISTRIBUTOR_LOCK_KEY, key = "#userId")
    public void applyAffairs(Long userId, String openid, ApplyAffairsDTO applyAffairs) {
        //检查是否开启了服务商模式
        if (paymentRpcService.serviceEnable() && StrUtil.isEmpty(openid)) {
            throw new GlobalException(DistributeErrorCode.WECHAT_NOT_BOUND, "请先绑定微信");
        }
        String mobile = applyAffairs.getMobile();
        String smsCode = applyAffairs.getCode();
        //检查验证码是否正确
        uaaRpcService.checkSmsCodeByType(SmsType.DISTRIBUTOR, mobile, smsCode);

        //校验是否可申请
        DistributeConf configMust = platformDistributeService.configMust();
        if (!configMust.getCondition().getTypes().contains(ConditionType.APPLY)) {
            throw new GlobalException(DistributeErrorCode.CANNOT_APPLY, "申请不可用");
        }
        //查询用户是否已存在
        Distributor distributor = getByUserId(userId).getOrNull();

        //不存在 生成新的用户信息
        distributor = getDistributor(userId, distributor);
        if (DistributorStatus.APPLYING == distributor.getStatus()) {
            throw new GlobalException(DistributeErrorCode.REPEATED_APPLY_SUBMISSION, "请勿重复提交");
        }
        if (DistributorIdentity.AFFAIRS == distributor.getIdentity()) {
            throw new GlobalException(DistributeErrorCode.USER_IS_DISTRIBUTOR_AFFAIRS, "已是分销商");
        }
        if (distributorService.lambdaQuery().eq(Distributor::getMobile, mobile).ne(Distributor::getUserId, userId)
                .exists()) {
            throw new GlobalException(DistributeErrorCode.MOBILE_BEAN_USED, "手机号已被使用");
        }

        distributor.setName(StrUtil.trim(applyAffairs.getName()))
                .setMobile(mobile)
                .setName(applyAffairs.getName())
                .setStatus(DistributorStatus.APPLYING)
                .setApplyTime(LocalDateTime.now());

        SystemCode.DATA_UPDATE_FAILED.falseThrow(
                distributorService.saveOrUpdate(distributor)
        );
    }


    @Override
    public IPage<Distributor> distributorPage(DistributorQueryDTO query) {
        IPage<Distributor> result = distributorService.distributorPage(query);
        List<Distributor> records = result.getRecords();
        if (CollUtil.isEmpty(records)) {
            return result;
        }
        //审核的分销商额外查询累计消费
//        if (DistributorStatus.APPLYING != query.getStatus()) {
//            return result;
//        }
        Set<Long> userIds = records.stream().map(Distributor::getUserId).collect(Collectors.toSet());
        Map<Long, Long> userConsumptionBatch = userRpcService.getUserConsumptionBatch(userIds);
        records.forEach(distributor -> distributor.setConsumption(
                userConsumptionBatch.getOrDefault(distributor.getUserId(), 0L)));
        return result;
    }


    @Override
    public DistributorRankPageVO rank(DistributorRankDTO query) {
        IPage<Distributor> page = distributorService.rank(query);
        DistributorRankPageVO result = new DistributorRankPageVO();
        result.setRecords(page.getRecords())
                .setCurrent(page.getCurrent())
                .setSize(page.getSize())
                .setTotal(page.getTotal());
        Option.of(query.getUserId())
                .peek(usrId -> result.setRank(distributorService.getUserRank(usrId, query.getShopId())));
        return result;
    }

    @Override
    public IPage<Distributor> distributorTeamPage(DistributorTeamQueryDTO query) {
        IPage<Distributor> result = distributorService.distributorTeamPage(query);
        ISecurity.match()
                .ifUser(
                        secureUser -> {
                            DistributorTeamQueryDTO queryPage = (DistributorTeamQueryDTO) result;
                            Level level = query.getLevel();
                            if (level == null) {
                                return;
                            }
                            Long userId = secureUser.getId();
                            switch (level) {
                                case ONE -> {
                                    queryPage.setCount1(queryPage.getTotal());
                                    queryPage.setCount2(
                                            distributorService.lambdaQuery().eq(Distributor::getTwo, userId).count());
                                    queryPage.setCount3(
                                            distributorService.lambdaQuery().eq(Distributor::getThree, userId).count());
                                }
                                case TWO -> {
                                    queryPage.setCount1(
                                            distributorService.lambdaQuery().eq(Distributor::getOne, userId).count());
                                    queryPage.setCount2(queryPage.getTotal());
                                    queryPage.setCount3(
                                            distributorService.lambdaQuery().eq(Distributor::getThree, userId).count());
                                }
                                case THREE -> {
                                    queryPage.setCount1(
                                            distributorService.lambdaQuery().eq(Distributor::getOne, userId).count());
                                    queryPage.setCount2(
                                            distributorService.lambdaQuery().eq(Distributor::getTwo, userId).count());
                                    queryPage.setCount3(queryPage.getTotal());
                                }
                                default -> {
                                }
                            }
                        }
                );
        return result;
    }

    @Override
    @Redisson(value = DistributeConstant.DISTRIBUTOR_LOCK_KEY, key = "#userId")
    public void updateDistributorStatus(Long userId, DistributorStatus currentStatus, DistributorStatus targetStatus) {
        Distributor distributor = distributorService.lambdaQuery()
                .eq(Distributor::getUserId, userId)
                .eq(Distributor::getStatus, currentStatus)
                .eq(Distributor::getIdentity, DistributorIdentity.USER)
                .one();
        if (distributor == null) {
            throw new GlobalException(DistributeErrorCode.APPLY_NOT_EXISTED, "申请不存在");
        }
        boolean isAffairsStatus = DistributorStatus.SUCCESS == targetStatus;
        SecureUser secureUser = ISecurity.userMust();
        boolean success = distributorService.lambdaUpdate()
                .set(isAffairsStatus, Distributor::getCode, RedisUtil.no(DistributeConstant.NO).toString())
                .set(isAffairsStatus, Distributor::getIdentity, DistributorIdentity.AFFAIRS)
                .set(Distributor::getPassTime, LocalDateTime.now())
                .set(Distributor::getAuditor, StrUtil.emptyToDefault(secureUser.getUsername(), secureUser.getMobile()))
                .set(Distributor::getStatus, targetStatus)
                .eq(Distributor::getUserId, userId)
                .eq(Distributor::getStatus, currentStatus)
                .eq(Distributor::getIdentity, DistributorIdentity.USER)
                .update();
        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);
    }

    @Override
    public Option<Distributor> getParentDistributorByUserId(Long userId) {
        Distributor currentUserDis = distributorService.lambdaQuery().eq(Distributor::getUserId, userId).one();
        if (currentUserDis == null || currentUserDis.getOne() == null) {
            return Option.none();
        }
        return Option.of(distributorService.lambdaQuery().eq(Distributor::getUserId, currentUserDis.getOne()).one());

    }

    @Override
    @Redisson(value = DistributeConstant.UPDATE_SHOP_LOCK_KEY, key = "#shopInfo.id")
    public void updateShopInfo(ShopInfoVO shopInfo) {
        distributeShopService.lambdaQuery()
                .eq(DistributeShop::getShopId, shopInfo.getId())
                .oneOpt()
                .ifPresent(
                        distributeShop -> distributeShopService.lambdaUpdate()
                                .eq(DistributeShop::getShopId, shopInfo.getId())
                                .set(DistributeShop::getShopName, shopInfo.getName())
                                .set(DistributeShop::getShopLogo, shopInfo.getLogo())
                                .update()
                );
    }

    @Override
    public void updateUserData(UserBaseDataDTO userData) {
        distributorService.lambdaUpdate()
                .set(Distributor::getNickname, userData.getNickname())
                .set(Distributor::getAvatar, userData.getAvatar())
                .eq(Distributor::getUserId, userData.getUserId())
                .update();
    }

    @Override
    public void applyAudit(DistributorAuditVo distributorAuditVo) {
        DistributorStatus targetStatus =
                distributorAuditVo.getPass() ? DistributorStatus.SUCCESS : DistributorStatus.CLOSED;
        SecureUser<?> secureUser = ISecurity.userMust();
        if (distributorAuditVo.getPass()) {
            //审核通过
            distributorAuditVo.getIds().forEach(
                    id -> {
                        boolean success = distributorService.lambdaUpdate()
                                .set(Distributor::getCode, RedisUtil.no(DistributeConstant.NO).toString())
                                .set(Distributor::getIdentity, DistributorIdentity.AFFAIRS)
                                //审核时间
                                .set(Distributor::getPassTime, LocalDateTime.now())
                                .set(Distributor::getAuditor,
                                        StrUtil.emptyToDefault(secureUser.getUsername(), secureUser.getMobile()))
                                .set(Distributor::getStatus, targetStatus)
                                .set(Distributor::getUpdateTime, LocalDateTime.now())
                                .eq(Distributor::getId, id)
                                .eq(Distributor::getStatus, DistributorStatus.APPLYING)
                                .eq(Distributor::getIdentity, DistributorIdentity.USER)
                                .update();
                        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);

                    }
            );
            return;
        }
        boolean success = distributorService.lambdaUpdate()
                .set(distributorAuditVo.getPass(), Distributor::getCode, RedisUtil.no(DistributeConstant.NO).toString())
                .set(distributorAuditVo.getPass(), Distributor::getIdentity, DistributorIdentity.AFFAIRS)
                //不管是否通过 审核时间都是这个
                .set(Distributor::getPassTime, LocalDateTime.now())
                .set(Distributor::getAuditor, StrUtil.emptyToDefault(secureUser.getUsername(), secureUser.getMobile()))
                .set(Distributor::getStatus, targetStatus)
                //未通过保存被拒绝的原因
                .set(!distributorAuditVo.getPass(), Distributor::getRejectReason, distributorAuditVo.getRejectReason())
                .set(Distributor::getUpdateTime, LocalDateTime.now())
                .in(Distributor::getId, distributorAuditVo.getIds())
                .eq(Distributor::getStatus, DistributorStatus.APPLYING)
                .eq(Distributor::getIdentity, DistributorIdentity.USER)
                .update();
        SystemCode.DATA_UPDATE_FAILED.falseThrow(success);

    }

    /**
     * 查询分销商被拒绝的原因
     *
     * @param id 分销商申请ID
     * @return
     */
    @Override
    public String queryRejectReason(Long id) {
        Distributor dbDistributor = distributorService.lambdaQuery().eq(Distributor::getId, id).one();
        if (Objects.isNull(dbDistributor)) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (!DistributorStatus.CLOSED.equals(dbDistributor.getStatus())) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
        return dbDistributor.getRejectReason();
    }

}
