package com.medusa.gruul.overview.service.model.dto;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.overview.api.enums.WithdrawType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * @author 张治保
 * date 2022/11/23
 */
@Getter
@Setter
@ToString
public class WithdrawAuditDTO implements BaseDTO {

    /**
     * 审核是否通过
     */
    @NotNull
    private Boolean pass;

    /**
     * 是否是线下已打款
     */
    @NotNull
    private Boolean offline;

    /**
     * 拒绝原因
     */
    private String reason;
    /**
     * 付款凭证
     */
    private String paymentVoucher;

    public void validParam(WithdrawType type) {
        //银行卡目前只能走线下支付方式
        SystemCode.PARAM_VALID_ERROR.trueThrow(WithdrawType.BANK_CARD == type && !getOffline());
        if (pass) {
            //银行卡目前只能走线下支付方式
            SystemCode.PARAM_VALID_ERROR.trueThrow(WithdrawType.BANK_CARD == type && !getOffline());
            //线下打款时必须上传凭证
            SystemCode.PARAM_VALID_ERROR.trueThrow(getOffline() && StrUtil.isEmpty(getPaymentVoucher()));
        }else {
            //拒绝时必须填写拒绝原因
            SystemCode.PARAM_VALID_ERROR.trueThrow(StrUtil.isEmpty(getReason()));
        }
    }
}
