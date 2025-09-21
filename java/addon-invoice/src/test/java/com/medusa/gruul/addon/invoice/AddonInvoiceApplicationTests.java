package com.medusa.gruul.addon.invoice;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.medusa.gruul.common.mp.model.BaseEntity;

import java.util.HashMap;

class AddonInvoiceApplicationTests {
    public static void main(String[] args) {
        FastAutoGenerator.create(
                        "jdbc:mysql://192.168.1.129:3306/gruul-addon-invoice",
                        "root",
                        "public2022"
                )
                .globalConfig(builder -> {
                    builder.author("")
                            //.enableSwagger()
                            //.fileOverride()
                            .outputDir("F:\\code\\qishan\\coding-mid-platform\\addon-invoice\\src\\main\\java");
                })
                .packageConfig(builder -> {
                    builder.parent("com.medusa.gruul.addon.invoice")
                            .moduleName("mp")
                            .pathInfo(
                                    new HashMap<OutputFile, String>() {
                                        private static final long serialVersionUID = 4195354017409294015L;

                                        {
                                            put(OutputFile.xml, "F:\\code\\qishan\\coding-mid-platform\\addon-invoice\\src\\main\\resources\\mapper");
                                        }
                                    }
                            );
                })
                .strategyConfig(builder -> {
                    builder.addInclude("t_invoice_attachment", "t_invoice_settings", "t_invoice_request")
                            .addTablePrefix("t_")
                            .entityBuilder()
                            .enableLombok()
                            .superClass(BaseEntity.class);
                }).execute();
    }

}

