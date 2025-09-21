package com.medusa.gruul.goods.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.goods.api.entity.CollectProductData;
import com.medusa.gruul.goods.service.mp.mapper.CollectProductDataMapper;
import com.medusa.gruul.goods.service.mp.service.ICollectProductDataService;
import org.springframework.stereotype.Service;

/**
 * 三方数据采集
 *
 * @author jipeng
 * @since 2024/11/1
 */
@Service
public class CollectProductDataServiceImpl extends ServiceImpl<CollectProductDataMapper, CollectProductData> implements ICollectProductDataService {
}
