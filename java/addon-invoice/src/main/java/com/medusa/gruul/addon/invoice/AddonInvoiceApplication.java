package com.medusa.gruul.addon.invoice;

import com.medusa.gruul.global.i18n.I18NPropertiesLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class AddonInvoiceApplication implements I18NPropertiesLoader {

    public static void main(String[] args) {
        SpringApplication.run(AddonInvoiceApplication.class, args);
    }

    @Override
    public String path() {
        return "i18n/invoice";
    }
}
