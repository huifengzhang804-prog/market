package com.medusa.gruul.addon.live.model;

import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 预告直播间
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class PreviewLiveRoomDTO extends PreviewLiveRoomInstantDTO implements BaseDTO {
    /**
     * 预计开播时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @Override
    public void validParam() {
        if (beginTime != null && beginTime.isBefore(LocalDateTime.now().plus(Duration.ofMinutes(15)))) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "预约时间不能小于当前时间");
        }
    }
}
