package com.medusa.gruul.search.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.search.api.model.ProductActivityVO;
import com.medusa.gruul.search.service.es.entity.EsProductActivityEntity;
import com.medusa.gruul.search.service.es.mapper.EsProductActivityMapper;
import com.medusa.gruul.search.service.service.ProductActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.core.kernel.EsWrappers;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 张治保 date 2023/3/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductActivityServiceImpl implements ProductActivityService {

    private final EsProductActivityMapper esProductActivityMapper;

    @Override
    public ProductActivityVO getRecentActivity(Long shopId, Long productId) {
        log.info("shopId:{}, productId:{}", shopId, productId);
        LocalDateTime now = LocalDateTime.now();
        List<EsProductActivityEntity> esProductActivityEntities = esProductActivityMapper.selectList(
                EsWrappers.lambdaQuery(EsProductActivityEntity.class)
                        .eq(EsProductActivityEntity::getShopId, shopId)
                        .eq(EsProductActivityEntity::getProductId, productId)
                        //未结束的活动
                        .gt(EsProductActivityEntity::getEndTime, FastJson2.DATETIME_FORMATTER.format(now))
                        .orderByAsc(EsProductActivityEntity::getStartTime)
                        .limit(CommonPool.NUMBER_ONE)
        );
        if (CollUtil.isEmpty(esProductActivityEntities)) {
            return null;
        }
        EsProductActivityEntity productActivityEntity = esProductActivityEntities.get(0);
        ProductActivityVO productActivityVO = new ProductActivityVO();
        BeanUtils.copyProperties(productActivityEntity, productActivityVO);
        return productActivityVO;
    }

}
