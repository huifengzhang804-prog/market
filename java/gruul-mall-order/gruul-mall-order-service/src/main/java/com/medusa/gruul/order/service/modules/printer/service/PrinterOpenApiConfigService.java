package com.medusa.gruul.order.service.modules.printer.service;

import com.medusa.gruul.order.service.modules.printer.feie.api.FeieConfig;
import com.medusa.gruul.order.service.modules.printer.model.dto.FeieConfigDTO;

/**
 * @author 张治保
 * @since 2024/8/22
 */
public interface PrinterOpenApiConfigService {

    /**
     * 设置 飞鹅打印机开放平台配置
     *
     * @param config 开放平台配置
     */
    void feie(FeieConfigDTO config);

    /**
     * 查询飞鹅打印机开放平台配置
     *
     * @return 飞鹅打印机开放平台配置
     */
    FeieConfig feieConfig();

}
