package com.medusa.gruul.payment.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.payment.service.mp.entity.PaymentNotifyRecord;
import com.medusa.gruul.payment.service.mp.mapper.PaymentNotifyRecordMapper;
import com.medusa.gruul.payment.service.mp.service.IPaymentNotifyRecordService;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * date 2023/6/7
 */
@Service
public class PaymentNotifyRecordServiceImpl extends ServiceImpl<PaymentNotifyRecordMapper, PaymentNotifyRecord> implements IPaymentNotifyRecordService {
}
