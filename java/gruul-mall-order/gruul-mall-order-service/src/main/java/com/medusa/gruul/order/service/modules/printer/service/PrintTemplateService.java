package com.medusa.gruul.order.service.modules.printer.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTemplateDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTemplatePageDTO;
import com.medusa.gruul.order.service.modules.printer.model.vo.PrintTemplateRecordVO;
import com.medusa.gruul.order.service.modules.printer.mp.entity.PrintTemplate;

/**
 * @author 张治保
 * @since 2024/8/21
 */
public interface PrintTemplateService {

    /**
     * 新增或更新打印模板
     *
     * @param shopId 店铺 id
     * @param param  打印机模板配置
     */
    void saveOrUpdate(Long shopId, PrintTemplateDTO param);

    /**
     * 分页查询打印机模板
     *
     * @param shopId 店铺 id
     * @param param  查询参数
     * @return 分页查询结果
     */
    IPage<PrintTemplateRecordVO> page(Long shopId, PrintTemplatePageDTO param);

    /**
     * 根据模板 id 查询模板配置详情
     *
     * @param id 模板 id
     * @return 模板配置详情
     */
    PrintTemplate detail(Long shopId, Long id);

    /**
     * 删除打印模板
     *
     * @param id 模板 id
     */
    void delete(Long shopId, Long id);

}
