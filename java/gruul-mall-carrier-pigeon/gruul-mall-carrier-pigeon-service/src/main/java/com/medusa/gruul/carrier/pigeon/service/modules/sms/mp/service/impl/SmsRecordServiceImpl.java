package com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto.SmsRecordDto;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.vo.SmsRecordVo;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsRecord;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.mapper.SmsRecordMapper;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.service.SmsRecordService;
import org.springframework.stereotype.Service;

/**
 * @author miskw
 * @date 2022/12/12
 */
@Service
public class SmsRecordServiceImpl extends ServiceImpl<SmsRecordMapper, SmsRecord> implements SmsRecordService {
    /**
     * 查询短信记录
     *
     * @param smsRecordDto 短信记录dto
     * @return 短信记录
     */
    @Override
    public IPage<SmsRecordVo> getSmsRecord(SmsRecordDto smsRecordDto) {
        return this.baseMapper.getSmsRecord(smsRecordDto);
    }
}
