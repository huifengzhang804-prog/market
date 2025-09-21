package com.medusa.gruul.addon.bargain.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.bargain.model.dto.BargainQueryDTO;
import com.medusa.gruul.addon.bargain.model.vo.BargainInfoVO;
import com.medusa.gruul.addon.bargain.mp.entity.Bargain;
import com.medusa.gruul.addon.bargain.mp.mapper.BargainMapper;
import com.medusa.gruul.addon.bargain.mp.service.IBargainService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 *
 * 砍价活动表 服务实现类
 * 
 *
 * @author WuDi
 * @since 2023-03-14
 */
@Service
public class BargainServiceImpl extends ServiceImpl<BargainMapper, Bargain> implements IBargainService {

    /**
     * 分页查询砍价活动
     * @param bargainQuery 砍价活动查询条件
     * @return 砍价活动分页
     */
    @Override
    public IPage<BargainInfoVO> bargainInfoPage(BargainQueryDTO bargainQuery) {
        return baseMapper.bargainInfoPage(bargainQuery);
    }
}
