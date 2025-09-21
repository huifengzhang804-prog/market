package com.medusa.gruul.addon.integral.model.dto;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.integral.model.enums.IntegralOrderDeliverType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 积分订单发货dto
 *
 * @author shishuqian
 * date 2023/2/6
 * time 11:23
 **/

@Getter
@Setter
@ToString
public class IntegralOrderDeliveryDTO implements BaseDTO {

    /**
     * 积分订单号
     */
    @NotBlank
    private String integralOrderNo;


    /**
     * 发货方式
     */
    @NotNull
    private IntegralOrderDeliverType integralOrderDeliverType;

    /**
     * 物流公司名称
     */
    private String expressName;

    /**
     * 物流公司编号
     */
    private String expressCompanyName;


    /**
     * 包裹运单号码
     */
    private String expressNo;

    @Override
    public void validParam() {
        if (integralOrderDeliverType == IntegralOrderDeliverType.EXPRESS) {
            if (StrUtil.isEmpty(expressName) || StrUtil.isEmpty(expressCompanyName) || StrUtil.isEmpty(expressNo)) {
                throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "配送信息不全，请填写完整");
            }
        }
    }
}
