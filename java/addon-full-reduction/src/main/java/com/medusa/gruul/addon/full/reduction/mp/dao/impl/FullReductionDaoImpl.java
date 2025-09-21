package com.medusa.gruul.addon.full.reduction.mp.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.full.reduction.mp.dao.FullReductionDao;
import com.medusa.gruul.addon.full.reduction.mp.entity.FullReduction;
import com.medusa.gruul.addon.full.reduction.mp.mapper.FullReductionMapper;
import org.springframework.stereotype.Service;

/**
 * 满减活动表 服务实现类
 *
 * @author WuDi
 * @since 2023-02-07
 */
@Service
public class FullReductionDaoImpl extends ServiceImpl<FullReductionMapper, FullReduction> implements FullReductionDao {
}
