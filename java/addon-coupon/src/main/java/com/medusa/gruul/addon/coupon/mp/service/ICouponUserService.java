package com.medusa.gruul.addon.coupon.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.coupon.model.dto.CouponUserDTO;
import com.medusa.gruul.addon.coupon.model.vo.CouponUserVO;
import com.medusa.gruul.addon.coupon.mp.entity.CouponUser;

/**
 * 优惠券 服务类
 *
 * @author 张治保
 * @since 2022-11-02
 */
public interface ICouponUserService extends IService<CouponUser> {


    /**
     * 用券记录
     *
     * @param query 查询参数
     * @return CouponVO
     */
    IPage<CouponUserVO> useRecords(CouponUserDTO query);


}
