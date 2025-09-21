package com.medusa.gruul.payment.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.payment.service.mp.entity.PaymentReceiver;
import com.medusa.gruul.payment.service.mp.mapper.PaymentReceiverMapper;
import com.medusa.gruul.payment.service.mp.service.IPaymentReceiverService;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * date 2023/6/8
 */
@Service
public class PaymentReceiverServiceImpl extends ServiceImpl<PaymentReceiverMapper, PaymentReceiver> implements IPaymentReceiverService {
}
