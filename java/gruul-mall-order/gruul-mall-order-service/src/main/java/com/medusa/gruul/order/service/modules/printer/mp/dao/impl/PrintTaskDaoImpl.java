package com.medusa.gruul.order.service.modules.printer.mp.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.order.service.modules.printer.mp.dao.PrintTaskDao;
import com.medusa.gruul.order.service.modules.printer.mp.entity.PrintTask;
import com.medusa.gruul.order.service.modules.printer.mp.mapper.PrintTaskMapper;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * @since 2024/8/22
 */
@Service
public class PrintTaskDaoImpl extends ServiceImpl<PrintTaskMapper, PrintTask> implements PrintTaskDao {
}
