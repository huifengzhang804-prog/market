package com.medusa.gruul.afs.service.model.dto;

import com.medusa.gruul.global.model.constant.RegexPools;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 到店退货
 *
 * @author xiaoq
 * @Description GoStoreRefundDTO.java
 * @date 2023-07-06 14:40
 */
@Getter
@Setter
@ToString
public class GoStoreRefundDTO {

    /**
     * 收货店员名称
     */
    private String shopAssistantName;


    /**
     * 电话
     */
    @Pattern(regexp = RegexPools.MOBILE_TEL)
    private String mobile;

    /**
     * 说明
     */
    private String explain;

}
