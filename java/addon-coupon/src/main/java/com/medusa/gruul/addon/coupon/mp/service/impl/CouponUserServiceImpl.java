package com.medusa.gruul.addon.coupon.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.coupon.model.dto.CouponUserDTO;
import com.medusa.gruul.addon.coupon.model.vo.CouponUserVO;
import com.medusa.gruul.addon.coupon.mp.entity.CouponUser;
import com.medusa.gruul.addon.coupon.mp.mapper.CouponUserMapper;
import com.medusa.gruul.addon.coupon.mp.service.ICouponUserService;
import org.springframework.stereotype.Service;

/**
 * 优惠券 服务实现类
 *
 * @author 张治保
 * @since 2022-11-02
 */
@Service
public class CouponUserServiceImpl extends ServiceImpl<CouponUserMapper, CouponUser> implements ICouponUserService {


    /**
     * 用券记录
     *
     * @param query  查询参数
     * @return CouponVO
     */
    @Override
    public IPage<CouponUserVO> useRecords(CouponUserDTO query) {
        return baseMapper.useRecords(query);
    }


}
