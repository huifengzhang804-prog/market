package com.medusa.gruul.order.service.modules.printer.mp.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.order.service.modules.printer.mp.dao.PrintTemplateDao;
import com.medusa.gruul.order.service.modules.printer.mp.entity.PrintTemplate;
import com.medusa.gruul.order.service.modules.printer.mp.mapper.PrintTemplateMapper;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * @since 2024/8/21
 */
@Service
public class PrinterDaoImpl extends ServiceImpl<PrintTemplateMapper, PrintTemplate> implements PrintTemplateDao {

}
