package com.medusa.gruul.addon.invoice.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceRequestQueryDTO;
import com.medusa.gruul.addon.invoice.model.vo.InvoiceVO;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceRequest;
import com.medusa.gruul.addon.invoice.mp.mapper.InvoiceRequestMapper;
import com.medusa.gruul.addon.invoice.mp.service.IInvoiceRequestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 发票申请表 服务实现类
 * </p>
 *
 * @since 2023-08-07
 */
@Service
public class InvoiceRequestServiceImpl extends ServiceImpl<InvoiceRequestMapper, InvoiceRequest> implements IInvoiceRequestService {


    /**
     * 分页查询开票申请
     *
     * @param invoiceRequestQuery 查询参数
     * @return 开票申请列表
     */
    @Override
    public IPage<InvoiceVO> pagInvoiceRequest(InvoiceRequestQueryDTO invoiceRequestQuery) {
        return baseMapper.pagInvoiceRequest(invoiceRequestQuery);
    }
}
