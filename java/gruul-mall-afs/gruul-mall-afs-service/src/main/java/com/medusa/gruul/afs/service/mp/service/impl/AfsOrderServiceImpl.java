package com.medusa.gruul.afs.service.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.afs.service.model.bo.AfsOrderQueryBO;
import com.medusa.gruul.afs.service.model.dto.AfsPageDTO;
import com.medusa.gruul.afs.service.model.dto.AfsQueryDTO;
import com.medusa.gruul.afs.service.mp.entity.AfsOrder;
import com.medusa.gruul.afs.service.mp.mapper.AfsOrderMapper;
import com.medusa.gruul.afs.service.mp.service.IAfsOrderService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 售后工单表 服务实现类
 * </p>
 *
 * @author 张治保
 * @since 2022-08-03
 */
@Service
public class AfsOrderServiceImpl extends ServiceImpl<AfsOrderMapper, AfsOrder> implements IAfsOrderService {

    @Override
    public IPage<AfsOrder> afsOrderPage(AfsPageDTO afsPage) {
        return baseMapper.afsOrderPage(afsPage);
    }

    @Override
    public List<AfsOrder> getAfsOrderDetail(AfsOrderQueryBO afsOrderQuery) {
        return baseMapper.getAfsOrderDetail(afsOrderQuery);
    }
 
    @Override
    public Integer staticsStatusCount(AfsQueryDTO afsQuery) {
        return baseMapper.staticsStatusCount(afsQuery);
    }
}
