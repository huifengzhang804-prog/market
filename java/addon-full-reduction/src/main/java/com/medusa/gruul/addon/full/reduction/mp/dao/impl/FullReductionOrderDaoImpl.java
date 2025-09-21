package com.medusa.gruul.addon.full.reduction.mp.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.full.reduction.mp.dao.FullReductionOrderDao;
import com.medusa.gruul.addon.full.reduction.mp.entity.FullReductionOrder;
import com.medusa.gruul.addon.full.reduction.mp.mapper.FullReductionOrderMapper;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * @since 2024/6/15
 */
@Service
public class FullReductionOrderDaoImpl extends ServiceImpl<FullReductionOrderMapper, FullReductionOrder> implements FullReductionOrderDao {
}
