package com.medusa.gruul.addon.coupon.mp.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.coupon.mp.entity.CouponCalculate;
import com.medusa.gruul.addon.coupon.mp.mapper.CouponCalculateMapper;
import com.medusa.gruul.addon.coupon.mp.service.ICouponCalculateService;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

/**
 * 服务实现类
 *
 * @author 张治保
 * @since 2022-11-07
 */
@Service
public class CouponCalculateServiceImpl extends ServiceImpl<CouponCalculateMapper, CouponCalculate> implements ICouponCalculateService {

    /**
     * 在同一个事务中处理 不影响其它业务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public <T> T todo(List<CouponCalculate> couponCalculates, Function<String, T> dataFunc) {
        String bid = IdUtil.simpleUUID();
        couponCalculates.forEach(couponCalculate -> couponCalculate.setBid(bid));
        boolean success = this.saveBatch(couponCalculates);
        if (!success) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED);
        }
        try {
            return dataFunc.apply(bid);
        } finally {
            this.lambdaUpdate().eq(CouponCalculate::getBid, bid).remove();
        }
    }
}
