package com.medusa.gruul.goods.service.functions.copy;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.global.model.strategy.AbstractStrategyFactory;
import com.medusa.gruul.global.model.strategy.IStrategy;
import com.medusa.gruul.goods.api.enums.CopyGoodsType;
import com.medusa.gruul.goods.api.model.enums.GoodsError;
import com.medusa.gruul.goods.service.functions.copy.strategy.Alibaba1688Strategy;
import com.medusa.gruul.goods.service.functions.copy.strategy.JingDongStrategy;
import com.medusa.gruul.goods.service.functions.copy.strategy.TaoBaoStrategy;
import com.medusa.gruul.goods.service.model.param.CopyGoodsParam;
import com.medusa.gruul.goods.service.model.vo.CopyProductVO;
import com.medusa.gruul.goods.service.mp.service.ICollectProductDataService;
import com.medusa.gruul.goods.service.properties.GoodsConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * 商品一键采集
 *
 * @author 张治保
 * @since 2024/4/10
 */
@Slf4j
@Component
public class ProductCollectorFactory extends AbstractStrategyFactory<CopyGoodsType, CopyGoodsParam, CopyProductVO> {

    public static PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    public static String copyApiKey;
    public static Boolean useCache;
    public static ICollectProductDataService collectProductDataService;
    public static Executor globalExecutor;

    public ProductCollectorFactory(PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService,
                                   GoodsConfigurationProperties goodsConfigurationProperties,
                                   ICollectProductDataService collectProductDataService,
                                   Executor globalExecutor) {
        ProductCollectorFactory.pigeonChatStatisticsRpcService = pigeonChatStatisticsRpcService;
        ProductCollectorFactory.copyApiKey = goodsConfigurationProperties.getCopyApikey();
        ProductCollectorFactory.useCache = goodsConfigurationProperties.isUseCache();
        ProductCollectorFactory.collectProductDataService = collectProductDataService;
        ProductCollectorFactory.globalExecutor = globalExecutor;
        if (StrUtil.isEmpty(ProductCollectorFactory.copyApiKey)) {
            log.warn("99api key(gruul.goods.copy-api-key)未配置，商品一键采集无法使用");
            log.warn("99api key(gruul.goods.copy-api-key)未配置，商品一键采集无法使用");
            log.warn("99api key(gruul.goods.copy-api-key)未配置，商品一键采集无法使用");
            throw GoodsError.API_EXCEPTION_99API.exception();
        }
    }

    @Override
    public Map<CopyGoodsType, Supplier<? extends IStrategy<CopyGoodsType, CopyGoodsParam, CopyProductVO>>> getStrategyMap() {
        if (StrUtil.isEmpty(ProductCollectorFactory.copyApiKey)) {
            log.error("未配置 99api key无法采集");
            throw GoodsError.API_EXCEPTION_99API.exception("99api key未配置", "商品一键采集无法使用");
        }
        return Map.of(
                CopyGoodsType.AliBaBa, Alibaba1688Strategy::new,
                CopyGoodsType.JD, JingDongStrategy::new,
                CopyGoodsType.TaoBao, TaoBaoStrategy::new
        );
    }

}


