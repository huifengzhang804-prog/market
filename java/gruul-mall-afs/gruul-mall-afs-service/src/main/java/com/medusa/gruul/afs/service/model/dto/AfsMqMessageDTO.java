package com.medusa.gruul.afs.service.model.dto;

import com.medusa.gruul.common.module.app.afs.AfsStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/23
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AfsMqMessageDTO {

    /**
     * 售后工单号
     */
    private String afsNo;

    /**
     * 目标售后状态
     */
    private AfsStatus currentStatus;

}
