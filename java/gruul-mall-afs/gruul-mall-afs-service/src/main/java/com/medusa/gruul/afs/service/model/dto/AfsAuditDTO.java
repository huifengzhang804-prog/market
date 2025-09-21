package com.medusa.gruul.afs.service.model.dto;

import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.global.model.express.UserAddressDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * <p></p>
 *
 * @author 张治保
 * date 2022/8/9
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AfsAuditDTO {
    /**
     * 是否同意售后申请
     */
    @NotNull
    private Boolean agree;

    /**
     * 目标关闭前的售后状态
     */
    private AfsStatus currentStatus;

    /**
     * 退货退款申请 同意时需要选择的收货地址
     */
    private UserAddressDTO receiver;
    /**
     * 备注
     */
    private String remark;

}
