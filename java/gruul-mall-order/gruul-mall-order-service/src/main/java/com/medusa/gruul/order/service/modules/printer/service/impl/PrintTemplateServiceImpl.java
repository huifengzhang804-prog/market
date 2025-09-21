package com.medusa.gruul.order.service.modules.printer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTemplateDTO;
import com.medusa.gruul.order.service.modules.printer.model.dto.PrintTemplatePageDTO;
import com.medusa.gruul.order.service.modules.printer.model.vo.PrintTemplateRecordVO;
import com.medusa.gruul.order.service.modules.printer.mp.dao.PrintTemplateDao;
import com.medusa.gruul.order.service.modules.printer.mp.entity.PrintTemplate;
import com.medusa.gruul.order.service.modules.printer.service.PrintTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * @since 2024/8/21
 */
@Service
@RequiredArgsConstructor
public class PrintTemplateServiceImpl implements PrintTemplateService {

    private final PrintTemplateDao printTemplateDao;

    @Override
    public void saveOrUpdate(Long shopId, PrintTemplateDTO param) {
        PrintTemplate printTemplate = new PrintTemplate()
                .setShopId(shopId)
                .setMode(param.getMode())
                .setName(param.getName())
                .setBrand(param.getBrand())
                .setLink(param.getLink())
                .setSize(param.getSize())
                .setConfig(param.getConfig())
                .setTemplate(param.getConfig().fxml(param.getSize()));
        if (param.getId() != null) {
            printTemplate.setId(param.getId());
        }
        printTemplateDao.saveOrUpdate(printTemplate);
    }

    @Override
    public IPage<PrintTemplateRecordVO> page(Long shopId, PrintTemplatePageDTO param) {
        PrintTemplatePageDTO page = printTemplateDao.lambdaQuery()
                .select(PrintTemplate::getId, PrintTemplate::getName, PrintTemplate::getBrand, PrintTemplate::getLink, PrintTemplate::getSize, PrintTemplate::getCreateTime)
                .eq(PrintTemplate::getShopId, shopId)
                .eq(param.getMode() != null, PrintTemplate::getMode, param.getMode())
                .eq(param.getTicketSize() != null, PrintTemplate::getSize, param.getTicketSize())
                .eq(param.getLink() != null, PrintTemplate::getLink, param.getLink())
                .orderByDesc(PrintTemplate::getCreateTime)
                .page(param);
        return page.convert(
                (record) -> new PrintTemplateRecordVO()
                        .setId(record.getId())
                        .setName(record.getName())
                        .setBrand(record.getBrand())
                        .setLink(record.getLink())
                        .setSize(record.getSize())
                        .setCreateDate(record.getCreateTime().toLocalDate())

        );
    }

    @Override
    public PrintTemplate detail(Long shopId, Long id) {
        return printTemplateDao.lambdaQuery()
                .select(BaseEntity::getId, PrintTemplate::getName, PrintTemplate::getBrand,
                        PrintTemplate::getLink, PrintTemplate::getSize, PrintTemplate::getConfig
                )
                .eq(PrintTemplate::getShopId, shopId)
                .eq(PrintTemplate::getId, id)
                .one();
    }

    @Override
    public void delete(Long shopId, Long id) {
        printTemplateDao.lambdaUpdate()
                .eq(PrintTemplate::getShopId, shopId)
                .eq(BaseEntity::getId, id)
                .remove();
    }
}
