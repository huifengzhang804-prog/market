package com.medusa.gruul.addon.distribute.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.medusa.gruul.addon.distribute.model.DistributeConstant;
import com.medusa.gruul.addon.distribute.model.DistributeErrorCode;
import com.medusa.gruul.addon.distribute.model.DistributorBonus;
import com.medusa.gruul.addon.distribute.model.dto.WithdrawDTO;
import com.medusa.gruul.addon.distribute.model.enums.DistributorIdentity;
import com.medusa.gruul.addon.distribute.model.vo.DistributorWithdraw;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;
import com.medusa.gruul.addon.distribute.mp.mapper.DistributorMapper;
import com.medusa.gruul.addon.distribute.mp.service.IDistributorService;
import com.medusa.gruul.addon.distribute.service.DistributeBonusService;
import com.medusa.gruul.addon.distribute.service.DistributorHandleService;
import com.medusa.gruul.addon.distribute.util.DistributeUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.overview.api.entity.OverviewWithdraw;
import com.medusa.gruul.overview.api.enums.DrawType;
import com.medusa.gruul.overview.api.enums.OverviewRabbit;
import com.medusa.gruul.overview.api.enums.OwnerType;
import com.medusa.gruul.overview.api.enums.WithdrawSourceType;
import com.medusa.gruul.overview.api.model.DrawTypeModel;
import com.medusa.gruul.overview.api.model.WithdrawOrderDTO;
import com.medusa.gruul.overview.api.rpc.OverviewRpcService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author 张治保
 * date 2023/5/16
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DistributeBonusServiceImpl implements DistributeBonusService, DisposableBean {

    private final RabbitTemplate rabbitTemplate;
    private final SqlSessionFactory sqlSessionFactory;
    private final IDistributorService distributorService;
    private final DistributorHandleService distributorHandleService;
    private final OverviewRpcService overviewRpcService;

    /**
     * todo 存在数据丢失的可能 ，后期更换为 redis存储
     */
    private final ConcurrentHashMap<Long, DistributorBonus> bonusLockMap = new ConcurrentHashMap<>();


    @Override
    public void shareBonusToUser(Map<Long, DistributorBonus> distributorBonusMap) {
        if (CollUtil.isEmpty(distributorBonusMap)) {
            return;
        }
        distributorBonusMap.forEach(
                (userId, currentBonus) -> {
                    long total = currentBonus.getTotal();
                    long undrawn = currentBonus.getUndrawn();
                    long unsettled = currentBonus.getUnsettled();
                    long invalid = currentBonus.getInvalid();
                    if (total == 0 && undrawn == 0 && unsettled == 0 && invalid == 0) {
                        return;
                    }
                    DistributorBonus bonus = bonusLockMap.computeIfAbsent(userId, key -> new DistributorBonus());
                    synchronized (bonus) {
                        bonus.setTotal(bonus.getTotal() + total)
                                .setUndrawn(bonus.getUndrawn() + undrawn)
                                .setUnsettled(bonus.getUnsettled() + unsettled)
                                .setInvalid(bonus.getInvalid() + invalid);
                    }
                }
        );
    }

    @Override
    public DistributorWithdraw withdrawCheck(String openid, Long userId) {
        Distributor distributor = distributorService.lambdaQuery()
                .select(Distributor::getId, Distributor::getUndrawn)
                .eq(Distributor::getUserId, userId)
                .eq(Distributor::getIdentity, DistributorIdentity.AFFAIRS)
                .one();
        if (distributor == null) {
            throw new GlobalException(DistributeErrorCode.AFFAIRS_NOT_EXISTED, "当前用户不是分销商");
        }
        return new DistributorWithdraw()
                .setUndrawn(distributor.getUndrawn())
                .setWechat(StrUtil.isNotBlank(openid));
    }

    @Override
    @Redisson(value = DistributeConstant.DISTRIBUTOR_BONUS_LOCK_KEY, key = "#secureUser.getId")
    @Transactional(rollbackFor = Exception.class)
    public void createWithdrawOrder(WithdrawDTO withdraw, SecureUser secureUser) {
        Long userId = secureUser.getId();
        //查询分销商信息
        Distributor distributor = distributorHandleService.getByUserId(userId).peek(peek -> {
            if (DistributorIdentity.AFFAIRS != peek.getIdentity()) {
                throw new GlobalException(DistributeErrorCode.AFFAIRS_NOT_EXISTED, "当前用户不是分销商");
            }
        }).getOrElseThrow(() -> new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, SystemCode.PARAM_VALID_ERROR.getMsg()));
        //查询提现账户
        Map<DrawType, JSONObject> accounts = overviewRpcService.getAccountsByUserId(userId);
        DrawType type = withdraw.getType();
        JSONObject account = accounts.get(type);
        if (type == DrawType.WECHAT) {
            String openid = secureUser.getOpenid();
            if (StrUtil.isEmpty(openid)) {
                throw new GlobalException(DistributeErrorCode.WECHAT_NOT_BOUND, "请先绑定微信");
            }
            account = new JSONObject().set("openid", openid);
        }
        if (account == null) {
            throw new GlobalException(DistributeErrorCode.WITHDRAW_ACCOUNT_NOT_EXISTED, "提现账户不存在");
        }
        //更新
        Long amount = withdraw.getAmount();
        boolean update = distributorService.lambdaUpdate()
                .eq(Distributor::getUserId, userId)
                .eq(Distributor::getIdentity, DistributorIdentity.AFFAIRS)
                .setSql(StrUtil.format(DistributeConstant.UNDRAWN_INCREMENT_SQL_TEMPLATE, -amount))
                .ge(Distributor::getUndrawn, amount)
                .update();
        SystemCode.DATA_UPDATE_FAILED.falseThrow(update);
        DrawTypeModel model = new DrawTypeModel().setAmount(amount);

        account = account.containsKey("raw") ? account.getJSONObject("raw") : account;
        if (type == DrawType.ALIPAY) {
            //兼容支付宝账户的名称 数据库中存放的是bank 而提取的时候用的是alipayAccount
            if (account.containsKey("bank")) {
                account.set("alipayAccount", account.get("bank"));
            }

        }
        DrawTypeModel dataTypeModel = type.getModelFunction().apply(model, account);
        rabbitTemplate.convertAndSend(
                OverviewRabbit.WITHDRAW_ORDER_CREATE.exchange(),
                OverviewRabbit.WITHDRAW_ORDER_CREATE.routingKey(),
                new WithdrawOrderDTO()
                        .setSourceType(WithdrawSourceType.DISTRIBUTE)
                        .setOwnerType(OwnerType.DISTRIBUTOR)
                        .setOwnerId(userId)
                        .setApplyUserPhone(distributor.getMobile())
                        .setName(distributor.getName())
                        .setAvatar(distributor.getAvatar())
                        .setContact(distributor.getMobile())
                        .setDrawType(dataTypeModel)
        );
    }


    @Override
    @Redisson(value = DistributeConstant.DISTRIBUTOR_BONUS_LOCK_KEY, key = "#overviewWithdraw.ownerId")
    public void withdrawOrderForbidden(OverviewWithdraw overviewWithdraw) {
        Long amount = overviewWithdraw.getDrawType().getAmount();
        this.shareBonusToUser(
                Map.of(
                        overviewWithdraw.getOwnerId(),
                        new DistributorBonus().setUndrawn(amount)
                )
        );
    }

    @Override
    public void changeBonusInvalidCommission(Map<Long, Long> bonusMap) {
        bonusMap.forEach((userId, amount) -> {
            TenantShop.disable(() -> {
                distributorService.changeBonusInvalidCommission(userId, amount);
            });

        });
    }

    /**
     * 定时任务处理用户分佣
     */
    @Scheduled(initialDelay = 5, fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    public void shareBonusToUser() {
        if (bonusLockMap.isEmpty()) {
            return;
        }
        //已当时的key为准
        Set<Long> userIds = bonusLockMap.keySet();
        //线程安全地取出key对应数据
        Map<Long, DistributorBonus> userBonus = new HashMap<>(userIds.size() * 4 / 3 + 1);
        for (Long userId : userIds) {
            //remove 删除 保证线程安全
            DistributorBonus currentUserBonus = bonusLockMap.remove(userId);
            if (currentUserBonus == null) {
                break;
            }
            userBonus.put(userId, currentUserBonus);
        }
        //批量更新
        this.shareBonusToUserBatch(userBonus);
    }

    @Override
    public void destroy() {
        this.shareBonusToUser();
    }

    /**
     * 批量佣金分摊 加锁保证数据准确性
     */
    private void shareBonusToUserBatch(Map<Long, DistributorBonus> userBonus) {
        if (CollUtil.isEmpty(userBonus)) {
            return;
        }
        //批量锁
        RedissonClient redissonClient = RedisUtil.getRedissonClient();
        RLock lock = redissonClient.getMultiLock(
                userBonus.keySet().stream()
                        .map(DistributeUtil::getDistributorBonusKey)
                        .map(redissonClient::getLock)
                        .toArray(RLock[]::new)
        );
        lock.lock();
        try {
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            try {
                DistributorMapper mapper = sqlSession.getMapper(DistributorMapper.class);
                userBonus.forEach(
                        (userId, bonus) -> {
                            if (bonus == null) {
                                return;
                            }
                            long total = bonus.getTotal();
                            long undrawn = bonus.getUndrawn();
                            long unsettled = bonus.getUnsettled();
                            long invalid = bonus.getInvalid();
                            if (total == 0 && undrawn == 0 && unsettled == 0 && invalid == 0) {
                                return;
                            }
                            LambdaUpdateWrapper<Distributor> updateWrapper = Wrappers.lambdaUpdate(Distributor.class)
                                    .eq(Distributor::getUserId, userId)
                                    .setSql(total != 0, StrUtil.format(DistributeConstant.TOTAL_INCREMENT_SQL_TEMPLATE, total))
                                    .setSql(undrawn != 0, StrUtil.format(DistributeConstant.UNDRAWN_INCREMENT_SQL_TEMPLATE, undrawn))
                                    .setSql(unsettled != 0, StrUtil.format(DistributeConstant.UNSETTLED_INCREMENT_SQL_TEMPLATE, unsettled))
                                    .setSql(invalid != 0, StrUtil.format(DistributeConstant.INVALID_INCREMENT_SQL_TEMPLATE, invalid));
                            mapper.update(null, updateWrapper);

                        }
                );
                sqlSession.commit();
            } catch (Exception exception) {
                sqlSession.rollback();
                this.shareBonusToUser(userBonus);
                throw exception;
            } finally {
                sqlSession.close();
            }
        } finally {
            lock.unlock();
        }

    }
}
