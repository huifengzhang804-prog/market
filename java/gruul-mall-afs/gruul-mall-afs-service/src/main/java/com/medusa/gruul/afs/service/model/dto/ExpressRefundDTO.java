package com.medusa.gruul.afs.service.model.dto;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.constant.RegexPools;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.express.ExpressCompanyDTO;
import com.medusa.gruul.order.api.enums.DeliverType;
import io.vavr.control.Option;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 快递退货
 *
 * @author xiaoq
 * @Description ExpressRefundDTO.java
 * @date 2023-07-06 14:40
 */
@Getter
@Setter
@ToString
public class ExpressRefundDTO {

    /**
     * 物流运输方式
     */
    @NotNull
    private DeliverType deliverType;

    /**
     * 物流公司信息
     */
    @Valid
    private ExpressCompanyDTO expressCompany;

    /**
     * 联系电话
     */
    @Pattern(regexp = RegexPools.MOBILE_TEL)
    private String mobile;
    /**
     * 物流备注
     */
    private String remark;

    public void validParam() {
        switch (getDeliverType()) {
            case PRINT_EXPRESS:
                throw new GlobalException("不支持的发货方式");
            case EXPRESS:
                Option.of(getExpressCompany())
                        .peek(
                                expressCompany -> {
                                    if (StrUtil.isBlank(expressCompany.getExpressNo())) {
                                        throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
                                    }
                                }
                        )
                        .getOrElseThrow(() -> new GlobalException("物流公司信息不能为空"));
                break;
            case WITHOUT:
                this.setExpressCompany(new ExpressCompanyDTO());
                break;
            default:
                break;
        }
    }
}
