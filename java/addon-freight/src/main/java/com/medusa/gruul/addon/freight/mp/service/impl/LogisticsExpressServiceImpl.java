package com.medusa.gruul.addon.freight.mp.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.freight.model.param.LogisticsExpressParam;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressExtendVO;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressUsableVO;
import com.medusa.gruul.addon.freight.model.vo.LogisticsExpressVO;
import com.medusa.gruul.addon.freight.mp.entity.LogisticsExpress;
import com.medusa.gruul.addon.freight.mp.mapper.LogisticsExpressMapper;
import com.medusa.gruul.addon.freight.mp.service.ILogisticsExpressService;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.system.model.ISystem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *
 *
 *
 *
 * @author xiaoq
 * @Description LogisticsExpressServiceImpl.java
 * @date 2022-06-20 10:41
 */
@Service
@RequiredArgsConstructor
public class LogisticsExpressServiceImpl extends ServiceImpl<LogisticsExpressMapper, LogisticsExpress> implements ILogisticsExpressService {
    private final LogisticsExpressMapper logisticsExpressMapper;

    @Override
    public IPage<LogisticsExpressVO> getLogisticsExpressList(LogisticsExpressParam logisticsExpressParam) {
        Long shopId = ISystem.shopIdMust();
        logisticsExpressParam.setShopId(shopId);
        return TenantShop.disable(() -> logisticsExpressMapper.queryLogisticsExpressList(logisticsExpressParam));
    }

    @Override
    public IPage<LogisticsExpressUsableVO> getLogisticsExpressUsableList(LogisticsExpressParam logisticsExpressParam) {
        Long shopId = ISystem.shopIdMust();
        logisticsExpressParam.setShopId(shopId);
        return TenantShop.disable(() -> logisticsExpressMapper.queryLogisticsExpressUsableList(logisticsExpressParam));
    }

    @Override
    public LogisticsExpressExtendVO getLogisticsExpressExtendInfo(String expressCompanyCode) {
        Long shopId = ISystem.shopIdMust();
        return TenantShop.disable(() -> logisticsExpressMapper.queryLogisticsExpressExtendInfo(expressCompanyCode, shopId));
    }
}
