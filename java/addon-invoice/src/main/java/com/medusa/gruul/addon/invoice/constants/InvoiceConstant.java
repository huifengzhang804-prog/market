package com.medusa.gruul.addon.invoice.constants;


public interface InvoiceConstant {

    String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String ORDER_COMPLETED_TIME = "completedTime";


    /**
     * UserInvoiceHandler
     */
    String USER_INVOICE_HANDLER  = "User";

    /**
     * ShopInvoiceHandler
     */
    String SHOP_INVOICE_HANDLER  = "Shop";
    String INVOICE_CONFIG_CACHE_KEY = "addon:invoice:config";

    String ORDER_BILL_MONEY = "billMoney";
}
