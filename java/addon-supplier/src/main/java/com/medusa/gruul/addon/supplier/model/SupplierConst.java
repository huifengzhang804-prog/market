package com.medusa.gruul.addon.supplier.model;

/**
 * 供应商常量
 *
 * @author 张治保
 * date 2023/7/19
 */
public interface SupplierConst {

    /**
     * 主订单号 前缀
     */
    String MAIN_NO_PREFIX = "MSP";

    /**
     * 主订单号 生成 key
     */
    String MAIN_NO_KEY = "addon:supplier:main:no";

    /**
     * 供应商订单号前缀
     */
    String NO_PREFIX = "SP";


    /**
     * 供应商订单号生成 key
     */
    String NO_KEY = "addon:supplier:order:no";


    /**
     * 订单信息创建结果缓存 key 前缀
     */
    String ORDER_CREATE_RESULT_PREFIX = "addon:supplier:order:create:result";

    /**
     * 供应商订单状态修改分布式锁 key 前缀
     */
    String ORDER_STATUS_UPDATE_LOCK_PREFIX = "addon:supplier:order:status:update:lock";

    /**
     * 订单超时时间配置缓存key
     */
    String ORDER_TIMEOUT_CONFIG_KEY = "addon:supplier:order:timeout";

    /**
     * 商品入库分布式锁
     */
    String PRODUCT_STORAGE_LOCK_PREFIX = "addon:supplier:product:storage:lock";

    /**
     * 商品发布分布式锁
     */
    String PRODUCT_PUBLISH_LOCK_PREFIX = "addon:supplier:product:publish:lock";
    /**
     * 采购订单付款方式
     */
    String ORDER_PAYMENT_METHOD="addon:supplier:order:payment:method";


    /**
     * 日期格式正则表达式
     */
    String DATE_REGEX = "^(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)$";

    String ADDON_SUPPLIER_NEW_COUNT_PRODUCT_KEY = "newCreatedProduct";
    String ADDON_SUPPLIER_IRREGULARITY_PRODUCT_KEY = "irregularityProduct";

}
