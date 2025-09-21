package com.medusa.gruul.payment.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.payment.api.entity.PaymentInfo;
import com.medusa.gruul.payment.service.mp.mapper.PaymentInfoMapper;
import com.medusa.gruul.payment.service.mp.service.IPaymentInfoService;
import org.springframework.stereotype.Service;

/**
 *
 *
 *
 *
 * @author xiaoq
 * @ Description
 * @date 2022-07-27 15:08
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements IPaymentInfoService {
}
