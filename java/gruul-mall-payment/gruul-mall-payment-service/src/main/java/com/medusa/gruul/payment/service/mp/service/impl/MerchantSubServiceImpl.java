package com.medusa.gruul.payment.service.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.payment.service.mp.entity.MerchantSub;
import com.medusa.gruul.payment.service.mp.mapper.MerchantSubMapper;
import com.medusa.gruul.payment.service.mp.service.IMerchantSubService;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * date 2023/6/5
 */
@Service
public class MerchantSubServiceImpl extends ServiceImpl<MerchantSubMapper, MerchantSub> implements IMerchantSubService {
}
