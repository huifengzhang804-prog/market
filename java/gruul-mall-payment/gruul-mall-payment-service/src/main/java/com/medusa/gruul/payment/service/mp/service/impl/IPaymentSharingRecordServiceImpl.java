package com.medusa.gruul.payment.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.payment.service.mp.entity.PaymentSharingRecord;
import com.medusa.gruul.payment.service.mp.mapper.PaymentSharingRecordMapper;
import com.medusa.gruul.payment.service.mp.service.IPaymentSharingRecordService;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * date 2023/6/20
 */
@Service
public class IPaymentSharingRecordServiceImpl extends ServiceImpl<PaymentSharingRecordMapper, PaymentSharingRecord> implements IPaymentSharingRecordService {
}
