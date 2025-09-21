package com.medusa.gruul.addon.invoice.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceHeaderQueryDTO;
import com.medusa.gruul.addon.invoice.model.vo.InvoiceHeadVO;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceHeader;
import org.apache.ibatis.annotations.Param;

/**
 * <p>发票抬头Mapper接口</p>
 * @author An.Yan
 */
public interface InvoiceHeaderMapper extends BaseMapper<InvoiceHeader> {

    /**
     * 分页查询发票抬头
     * @param invoiceHeaderQuery 查询参数
     * @return 发票抬头列表
     */
    IPage<InvoiceHeadVO> pageInvoiceHeader(@Param("invoiceHeaderQuery") InvoiceHeaderQueryDTO invoiceHeaderQuery);
}
