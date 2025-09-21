package com.medusa.gruul.addon.distribute.service.impl;

import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.medusa.gruul.addon.distribute.model.DistributeConstant;
import com.medusa.gruul.addon.distribute.model.DistributeErrorCode;
import com.medusa.gruul.addon.distribute.model.dto.DistributeConfDTO;
import com.medusa.gruul.addon.distribute.model.enums.Level;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeConf;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeProduct;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;
import com.medusa.gruul.addon.distribute.mp.service.IDistributeConfService;
import com.medusa.gruul.addon.distribute.mp.service.IDistributeProductService;
import com.medusa.gruul.addon.distribute.mp.service.IDistributorService;
import com.medusa.gruul.addon.distribute.properties.DistributeConfigurationProperties;
import com.medusa.gruul.addon.distribute.service.DistributeConfHandleService;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.exception.GlobalException;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * @author 张治保
 * date 2022/11/15
 */
@Service
@RequiredArgsConstructor
public class DistributeConfHandleServiceImpl implements DistributeConfHandleService {

    private final IDistributorService distributorService;
    private final IDistributeConfService distributeConfService;
    private final IDistributeProductService distributeProductService;
    private final DistributeConfigurationProperties configurationProperties;

    @Override
    public Option<DistributeConf> config() {
        return Option.of(
                RedisUtil.getCacheMap(
                        DistributeConf.class,
                        () -> distributeConfService.lambdaQuery().one(),
                        Duration.ofSeconds(configurationProperties.getCacheExpire().getConfig()),
                        DistributeConstant.DISTRIBUTE_CONFIG_CACHE_KEY
                )
        );
    }

    @Override
    public DistributeConf configMust() {
        return config().getOrElseThrow(() -> new GlobalException(DistributeErrorCode.CONFIG_NOT_EXISTS, "分销活动还未开启"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(value = DistributeConstant.DISTRIBUTE_CONFIG_UPDATE_LOCK_KEY)
    public void updateConf(DistributeConfDTO distributeConf) {
        DistributeConf config = config().getOrElse(DistributeConf::new);

        Level preLevel = config.getLevel();
        Level curLevel = distributeConf.getLevel();

        config.setLevel(curLevel)
                .setCondition(distributeConf.getCondition())
                .setProtocol(distributeConf.getProtocol())
                .setPlayMethods(distributeConf.getPlayMethods())
                .setPrecompute(distributeConf.getPrecompute())
                .setPurchase(distributeConf.getPurchase())
                .setShareType(distributeConf.getShareType())
                .setOne(distributeConf.getOne())
                .setTwo(distributeConf.getTwo())
                .setThree(distributeConf.getThree());
        RedisUtil.doubleDeletion(
                () -> distributeConfService.saveOrUpdate(config),
                DistributeConstant.DISTRIBUTE_CONFIG_CACHE_KEY
        );
        //新创建 或者 相同 或者  新分销层级比较大 直接跳过
        if (preLevel == null || curLevel == preLevel || curLevel.getValue() > preLevel.getValue()) {
            return;
        }
        //否则是降 分销层级 可能需要设置 把所有分销用户三级 甚至二级置为空
        //分销商绑定关系
        LambdaUpdateChainWrapper<Distributor> distributorUpdateWrapper = distributorService.lambdaUpdate()
                .set(Distributor::getThree, null);
        //商品分销关系
        LambdaUpdateChainWrapper<DistributeProduct> productUpdateWrapper = distributeProductService.lambdaUpdate()
                .set(DistributeProduct::getThree, null);
        if (curLevel == Level.ONE) {
            distributorUpdateWrapper.set(Distributor::getTwo, null);
            productUpdateWrapper.set(DistributeProduct::getTwo, null);
        }
        distributorUpdateWrapper.update();
        productUpdateWrapper.update();
    }


}
