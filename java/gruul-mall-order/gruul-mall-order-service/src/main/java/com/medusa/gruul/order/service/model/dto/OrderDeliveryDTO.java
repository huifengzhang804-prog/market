package com.medusa.gruul.order.service.model.dto;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.global.model.express.ExpressCompanyDTO;
import com.medusa.gruul.global.model.express.UserAddressDTO;
import com.medusa.gruul.order.api.enums.DeliverType;
import com.medusa.gruul.order.api.enums.SelfShopTypeEnum;
import com.medusa.gruul.order.service.model.enums.OrderError;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 订单发货参数
 *
 * @author 张治保
 * date 2022/7/27
 */
@Getter
@Setter
@ToString
public class OrderDeliveryDTO {

    /**
     * 店铺 id 供应商操作时比传
     */
    private Long shopId;

    /**
     * 发货方式
     */
    @NotNull
    private DeliverType deliverType;

    /**
     * 是否直接发货 当 deliverType为PRINT时必填
     */
    private Boolean dropDeliver = Boolean.FALSE;

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
     * 自营店铺类型
     */
    private SelfShopTypeEnum selfShopType;
    
    /**
     * 商品销售类型
     */
    private SellType sellType;

    /**
     * 订单发货商品详情
     */
    @NotNull
    @Size(min = 1)
    @Valid
    private List<OrderDeliveryItemDTO> items;


    public void validParam() {
        DeliverType deliverType = this.getDeliverType();
        //无需物流时
        if (DeliverType.WITHOUT == deliverType) {
            return;
        }
        if (receiver == null) {
            throw OrderError.RECEIVER_NOT_NULL.exception();
        }
        if (deliverType.isNeedExpress()) {
            ExpressCompanyDTO expressComp = getExpressCompany();
            if (expressComp == null) {
                throw OrderError.LOGISTICS_COMPANY_NOT_NULL.exception();
            }
            if (StrUtil.isEmpty(expressComp.getExpressCompanyName()) || StrUtil.isEmpty(expressComp.getExpressCompanyCode())) {
                throw OrderError.LOGISTICS_COMPANY_NAME_AND_CODE_NOT_NULL.exception();
            }
            if (DeliverType.EXPRESS == deliverType && StrUtil.isEmpty(expressComp.getExpressNo())) {
                throw OrderError.EXPRESS_NUMBER_NOT_NULL.exception();
            }
        }
        if (DeliverType.PRINT_EXPRESS == deliverType && getSender() == null) {
            throw OrderError.SENDER_ADDRESS_NOT_NULL.exception();
        }
    }


}
