package com.medusa.gruul.order.service.modules.printer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrinterEditDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrinterPageDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrinterSaveDTO;
import com.medusa.gruul.order.service.modules.printer.model.vo.PrinterRecordVO;

/**
 * @author 张治保
 * @since 2024/8/21
 */
public interface PrinterService {

    /**
     * 新增或更新打印机
     *
     * @param shopId 店铺 id
     * @param param  打印机参数
     */
    void save(Long shopId, PrinterSaveDTO param);

    /**
     * 编辑打印机
     *
     * @param shopId 店铺id
     * @param param  打印机编辑参数
     */
    void edit(Long shopId, PrinterEditDTO param);

    /**
     * 分页查询打印机
     *
     * @param shopId 店铺 id
     * @param param  分页查询参数
     * @return 分页查询结果
     */
    IPage<PrinterRecordVO> page(Long shopId, PrinterPageDTO param);

    /**
     * 删除打印机
     *
     * @param shopId 店铺 id
     * @param id     打印机 id
     */
    void delete(Long shopId, Long id);
}
