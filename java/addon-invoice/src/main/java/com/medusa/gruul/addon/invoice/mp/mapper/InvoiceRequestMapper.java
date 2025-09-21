package com.medusa.gruul.addon.invoice.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceRequestQueryDTO;
import com.medusa.gruul.addon.invoice.model.vo.InvoiceVO;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceRequest;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 发票申请表 Mapper 接口
 * </p>
 *
 * @since 2023-08-07
 */
public interface InvoiceRequestMapper extends BaseMapper<InvoiceRequest> {

    /**
     * 分页查询开票申请
     *
     * @param invoiceRequestQuery 查询参数
     * @return 开票申请列表
     */
    IPage<InvoiceVO> pagInvoiceRequest(@Param("invoiceRequestQuery") InvoiceRequestQueryDTO invoiceRequestQuery);

}
