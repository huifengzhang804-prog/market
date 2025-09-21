package com.medusa.gruul.addon.bargain.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.addon.bargain.model.BargainKey;
import com.medusa.gruul.addon.bargain.model.enums.BargainStatus;
import com.medusa.gruul.addon.bargain.mp.entity.BargainOrder;
import com.medusa.gruul.addon.bargain.mp.entity.BargainProduct;
import com.medusa.gruul.addon.bargain.mp.service.IBargainOrderService;
import com.medusa.gruul.addon.bargain.mp.service.IBargainProductService;
import com.medusa.gruul.addon.bargain.service.BargainManageService;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author wudi
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BargainXxlJobHandler {

    private final BargainManageService bargainService;
    private final IBargainOrderService bargainOrderService;
    private final IBargainProductService bargainProductService;


    @Transactional(rollbackFor = Exception.class)
    public void execute() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("{} 定时任务开始", jobParam);
        if (StrUtil.isEmpty(jobParam)) {
            return;
        }
        BargainKey bargainKey = JSON.parseObject(jobParam, BargainKey.class);
        Long shopId = bargainKey.getShopId();
        Long activityId = bargainKey.getActivityId();
        Set<Long> productIds = bargainKey.getProductIds();
        // 归还砍价活动库存
        bargainService.returnStorageSku(shopId, activityId, productIds);
        boolean exists = bargainOrderService.lambdaQuery()
                .eq(BargainOrder::getActivityId, activityId)
                .eq(BargainOrder::getShopId, shopId)
                .eq(BargainOrder::getBargainStatus, BargainStatus.BARGAINING)
                .exists();
        if (!exists) {
            return;
        }
        // 更新砍价活动状态
        bargainOrderService.lambdaUpdate()
                .set(BargainOrder::getBargainStatus, BargainStatus.FAILED_TO_BARGAIN)
                .eq(BargainOrder::getActivityId, activityId)
                .eq(BargainOrder::getShopId, shopId)
                .update();
    }
}
