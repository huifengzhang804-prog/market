package com.medusa.gruul.addon.invoice.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceHeaderQueryDTO;
import com.medusa.gruul.addon.invoice.model.vo.InvoiceHeadVO;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceHeader;

public interface IInvoiceHeaderService extends IService<InvoiceHeader> {

    /**
     * 分页查询发票抬头
     * @param invoiceHeaderQuery 查询参数
     * @return 发票抬头列表
     */
    IPage<InvoiceHeadVO> pageInvoiceHeader(InvoiceHeaderQueryDTO invoiceHeaderQuery);
}
