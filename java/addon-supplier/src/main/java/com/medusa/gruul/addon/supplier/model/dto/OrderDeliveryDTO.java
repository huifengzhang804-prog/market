package com.medusa.gruul.addon.supplier.model.dto;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.express.ExpressCompanyDTO;
import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.order.api.enums.DeliverType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * 订单发货参数
 *
 * @author 张治保
 * date 2022/7/27
 */
@Getter
@Setter
@EqualsAndHashCode(of = "orderNo")
@ToString
public class OrderDeliveryDTO {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 发货方式
     */
    @NotNull
    private DeliverType deliverType;

    /**
     * 是否直接发货 当 deliverType为PRINT时必填
     */
    private Boolean dropDeliver;

    /**
     * 打印发货需要 打印机id
     */
    private Long printId;

    /**
     * 打印发货需要
     */
    private UserAddressDTO sender;

    /**
     * 收货人 当 deliverType为WITHOUT时 不必填 其它必填
     */
    @Valid
    private UserAddressDTO receiver;

    /**
     * 商品描述
     */
    private String cargo;

    /**
     * 备注
     */
    private String remark;

    /**
     * 物流公司
     */
    @Valid
    private ExpressCompanyDTO expressCompany;

    /**
     * 订单发货商品详情
     */
    @NotNull
    @Size(min = 1)
    @Valid
    private Set<OrderDeliveryItemDTO> items;


    public void validParam() {
        DeliverType deliverType = this.getDeliverType();
        //无需物流时
        if (DeliverType.WITHOUT == deliverType) {
            return;
        }
        if (receiver == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        ExpressCompanyDTO expressComp = getExpressCompany();
        if (expressComp == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (StrUtil.isEmpty(expressComp.getExpressCompanyName()) || StrUtil.isEmpty(expressComp.getExpressCompanyCode())) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (DeliverType.EXPRESS == deliverType && StrUtil.isEmpty(expressComp.getExpressNo())) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (DeliverType.PRINT_EXPRESS == deliverType) {
            if (getDropDeliver() == null) {
                setDropDeliver(false);
            }
            if (getSender() == null) {
                throw SystemCode.DATA_NOT_EXIST.exception();
            }
        }
    }


}
