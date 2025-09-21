package com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.dto.SmsRecordDto;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.model.vo.SmsRecordVo;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity.SmsRecord;
import org.apache.ibatis.annotations.Param;

/**
 * @author miskw
 * @date 2022/12/12
 */
public interface SmsRecordMapper extends BaseMapper<SmsRecord> {
    /**
     * 查询短信记录
     *
     * @param smsRecordDto 短信记录DTO
     * @return 短信记录
     */
    IPage<SmsRecordVo> getSmsRecord(@Param("smsRecordDto") SmsRecordDto smsRecordDto);
}
