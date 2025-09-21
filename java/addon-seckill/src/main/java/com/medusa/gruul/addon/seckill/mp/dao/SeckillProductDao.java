package com.medusa.gruul.addon.seckill.mp.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.seckill.model.dto.SeckillRoundProductPageDTO;
import com.medusa.gruul.addon.seckill.model.vo.SeckillRoundProductVO;
import com.medusa.gruul.addon.seckill.mp.entity.SeckillProduct;

/**
 * @author 张治保
 * @since 2024/5/28
 */
public interface SeckillProductDao extends IService<SeckillProduct> {

    /**
     * 分页查询秒杀活动场次商品
     *
     * @param page 分页查询参数
     * @return 分页查询结果
     */
    IPage<SeckillRoundProductVO> roundProductPage(SeckillRoundProductPageDTO page);
}
