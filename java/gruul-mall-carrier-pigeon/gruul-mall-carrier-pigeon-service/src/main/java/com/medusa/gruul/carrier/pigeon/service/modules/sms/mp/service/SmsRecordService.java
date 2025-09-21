package com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto.SmsRecordDto;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.vo.SmsRecordVo;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsRecord;

/**
 * @author miskw
 * @date 2022/12/12
 */
public interface SmsRecordService extends IService<SmsRecord> {
    /**
     * 查询短信记录
     *
     * @param smsRecordDto 短信DTO
     * @return 短信记录
     */
    IPage<SmsRecordVo> getSmsRecord(SmsRecordDto smsRecordDto);
}
