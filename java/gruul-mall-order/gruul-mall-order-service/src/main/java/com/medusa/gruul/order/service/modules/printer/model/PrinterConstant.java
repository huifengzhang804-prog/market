package com.medusa.gruul.order.service.modules.printer.model;

/**
 * @author 张治保
 * @since 2024/8/21
 */
public interface PrinterConstant {

    /**
     * 飞鹅配置缓存
     */
    String FEIE_CONFIG_CACHE_KEY = "gruul:mall:order:printer:config:feie";

    /**
     * 打印机任务添加时的分布式锁 避免重复添加同一种打印类型 （同一联只能添加一个）
     */
    String PRINT_TASK_LINK_LOCK_KEY = "gruul:mall:order:printTaskLink:lock";

    /**
     * 避免同一台打印机 反复绑定的分布式锁 key
     */
    String PRINTER_BIND_LOCK_KEY = "gruul:mall:order:printerAdd:lock";


    /**
     * 删除打印机分布式锁 避免打印机删除与添加打印模版时 存在冲突
     */
    String PRINTER_DELETE_LOCK_KEY = "gruul:mall:order:printerDel:lock";

}
