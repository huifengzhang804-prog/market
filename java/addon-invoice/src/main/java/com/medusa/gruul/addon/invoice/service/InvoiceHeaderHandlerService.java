package com.medusa.gruul.addon.invoice.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceHeaderCreateDTO;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceHeaderQueryDTO;
import com.medusa.gruul.addon.invoice.model.dto.SetDefaultInvoiceHeaderDTO;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceHeaderOwnerTypeEnum;
import com.medusa.gruul.addon.invoice.model.vo.InvoiceHeadVO;

public interface InvoiceHeaderHandlerService {

    /**
     * 创建发票抬头
     *
     * @param dto {@link InvoiceHeaderCreateDTO}
     */
    void create(InvoiceHeaderCreateDTO dto);

    /**
     * 分页查询发票抬头
     *
     * @param invoiceHeaderQuery 查询参数
     * @return 发票抬头列表
     */
    IPage<InvoiceHeadVO> pageInvoiceHeader(InvoiceHeaderQueryDTO invoiceHeaderQuery);

    /**
     * 删除发票抬头
     *
     * @param invoiceHeaderId 删除发票抬头
     */
    void deleteInvoiceHeader(Long invoiceHeaderId);


    /**
     * 设置默认抬头
     *
     * @param dto {@link SetDefaultInvoiceHeaderDTO}
     */
    void setDefaultInvoiceHeader(SetDefaultInvoiceHeaderDTO dto);

    /**
     * 获取发票抬头详情
     *
     * @param invoiceHeaderId        发票抬头id
     * @param invoiceHeaderOwnerType 发票抬头所属方类型
     */
    InvoiceHeadVO getInvoiceHeaderDetail(Long invoiceHeaderId, InvoiceHeaderOwnerTypeEnum invoiceHeaderOwnerType);


    /**
     * 获取默认抬头
     * @param ownId                   发票抬头所属者ID
     * @param invoiceHeaderOwnerType 发票抬头所属方类型
     * @return 默认抬头
     */
    InvoiceHeadVO getDefaultInvoiceHeader(Long ownId, InvoiceHeaderOwnerTypeEnum invoiceHeaderOwnerType);
}
