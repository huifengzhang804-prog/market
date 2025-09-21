package com.medusa.gruul.addon.seckill.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.seckill.model.dto.SeckillRoundProductPageDTO;
import com.medusa.gruul.addon.seckill.model.vo.SeckillRoundProductVO;
import com.medusa.gruul.addon.seckill.mp.entity.SeckillProduct;
import org.apache.ibatis.annotations.Param;

/**
 * @author 张治保
 * @since 2024/5/28
 */
public interface SeckillProductMapper extends BaseMapper<SeckillProduct> {

    /**
     * 分页查询秒杀活动场次商品
     *
     * @param page 分页查询参数
     * @return 分页查询结果
     */
    IPage<SeckillRoundProductVO> roundProductPage(@Param("page") SeckillRoundProductPageDTO page);
}
