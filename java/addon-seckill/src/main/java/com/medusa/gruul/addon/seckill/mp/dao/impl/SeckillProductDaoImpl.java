package com.medusa.gruul.addon.seckill.mp.dao.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.seckill.model.dto.SeckillRoundProductPageDTO;
import com.medusa.gruul.addon.seckill.model.vo.SeckillRoundProductVO;
import com.medusa.gruul.addon.seckill.mp.dao.SeckillProductDao;
import com.medusa.gruul.addon.seckill.mp.entity.SeckillProduct;
import com.medusa.gruul.addon.seckill.mp.mapper.SeckillProductMapper;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * @since 2024/5/28
 */
@Service
public class SeckillProductDaoImpl extends ServiceImpl<SeckillProductMapper, SeckillProduct> implements SeckillProductDao {
    @Override
    public IPage<SeckillRoundProductVO> roundProductPage(SeckillRoundProductPageDTO page) {
        return baseMapper.roundProductPage(page);
    }
}
