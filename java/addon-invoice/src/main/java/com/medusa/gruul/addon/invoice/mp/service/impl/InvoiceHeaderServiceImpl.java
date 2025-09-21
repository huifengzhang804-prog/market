package com.medusa.gruul.addon.invoice.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceHeaderQueryDTO;
import com.medusa.gruul.addon.invoice.model.vo.InvoiceHeadVO;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceHeader;
import com.medusa.gruul.addon.invoice.mp.mapper.InvoiceHeaderMapper;
import com.medusa.gruul.addon.invoice.mp.service.IInvoiceHeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceHeaderServiceImpl extends ServiceImpl<InvoiceHeaderMapper, InvoiceHeader> implements IInvoiceHeaderService {

    /**
     * 分页查询发票抬头
     * @param invoiceHeaderQuery 查询参数
     * @return 发票抬头列表
     */
    @Override
    public IPage<InvoiceHeadVO> pageInvoiceHeader(InvoiceHeaderQueryDTO invoiceHeaderQuery) {
        return baseMapper.pageInvoiceHeader(invoiceHeaderQuery);
    }
}
