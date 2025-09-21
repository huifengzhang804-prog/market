package com.medusa.gruul.addon.integral.mp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.integral.model.dto.IntegralOrderQueryDTO;
import com.medusa.gruul.addon.integral.model.dto.IntegralOrderRemarkDTO;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderStatus;
import com.medusa.gruul.addon.integral.model.vo.IntegralOrderListVO;
import com.medusa.gruul.addon.integral.mp.entity.IntegralOrder;
import com.medusa.gruul.addon.integral.mp.mapper.IntegralOrderMapper;
import com.medusa.gruul.addon.integral.mp.service.IIntegralOrderService;
import com.medusa.gruul.common.system.model.model.Platform;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xiaoq
 * @Description
 * @date 2023-01-31 14:52
 */
@Service
@RequiredArgsConstructor
public class IntegralOrderServiceImpl extends ServiceImpl<IntegralOrderMapper, IntegralOrder> implements IIntegralOrderService {


    @Override
    public IntegralOrder getUnpaidIntegralOrder(String integralOrderNo) {

        return this.lambdaQuery().eq(IntegralOrder::getNo, integralOrderNo)
                .eq(IntegralOrder::getStatus, IntegralOrderStatus.UNPAID)
                .one();

    }


    @Override
    public void integralOrderBatchRemark(IntegralOrderRemarkDTO integralOrderRemark) {

        String remark = StrUtil.trim(integralOrderRemark.getRemark());
        this.lambdaUpdate()
                .set(IntegralOrder::getSellerRemark, remark)
                .in(IntegralOrder::getNo, integralOrderRemark.getNos())
                .update();
    }


    @Override
    public IPage<IntegralOrderListVO> integralOrderPage(IntegralOrderQueryDTO integralOrderQueryDTO) {
        IPage<IntegralOrderListVO> integralOrderListVOIPage = this.baseMapper.integralOrderPage(integralOrderQueryDTO);
        // 获取未发货订单数量
        integralOrderQueryDTO.setWaitDeliveryNum(this.lambdaQuery().eq(IntegralOrder::getStatus, IntegralOrderStatus.PAID).count());
        return integralOrderListVOIPage;
    }

    @Override
    public IntegralOrder getIntegralOrderDetailInfo(String integralOrderNo) {
        return this.baseMapper.getIntegralOrderDetailInfo(integralOrderNo);
    }

    @Override
    public List<IntegralOrder> unDeliverBatch(IntegralOrderStatus paid) {
        return baseMapper.unDeliverBatch(paid);
    }

    @Override
    public IntegralOrder undeliver(String orderNo, IntegralOrderStatus status) {
        return baseMapper.undeliver(orderNo, status);
    }

    @Override
    public Long warningIntegralOrder() {
        return this.lambdaQuery()
                .eq(IntegralOrder::getStatus, IntegralOrderStatus.PAID)
                .eq(IntegralOrder::getAffiliationPlatform, Platform.WECHAT_MINI_APP).count();
    }
}
