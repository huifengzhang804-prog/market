package com.medusa.gruul.addon.coupon.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.coupon.model.dto.CouponUserDTO;
import com.medusa.gruul.addon.coupon.model.vo.CouponUserVO;
import com.medusa.gruul.addon.coupon.mp.entity.CouponUser;
import org.apache.ibatis.annotations.Param;

/**
 * 优惠券 Mapper 接口
 *
 * @author 张治保
 * @since 2022-11-02
 */
public interface CouponUserMapper extends BaseMapper<CouponUser> {

    /**
     * 用券记录
     *
     * @param query  查询参数
     * @return CouponVO
     */
    IPage<CouponUserVO> useRecords(@Param("query") CouponUserDTO query);


}
