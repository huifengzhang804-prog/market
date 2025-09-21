package com.medusa.gruul.payment.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.payment.api.entity.PaymentRecord;
import com.medusa.gruul.payment.service.mp.mapper.PaymentRecordMapper;
import com.medusa.gruul.payment.service.mp.service.IPaymentRecordService;
import org.springframework.stereotype.Service;

/**
 *
 *
 *
 *
 * @author xiaoq
 * @ Description
 * @date 2022-08-01 18:40
 */
@Service
public class PaymentRecordServiceImpl extends ServiceImpl<PaymentRecordMapper, PaymentRecord> implements IPaymentRecordService {
}
