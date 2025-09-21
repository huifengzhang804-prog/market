package com.medusa.gruul.addon.rebate;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.medusa.gruul.common.mp.model.BaseEntity;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
class AddonConsumptionRebateApplicationTests {

    public static void main(String[] args) {
        FastAutoGenerator.create(
                        "jdbc:mysql://192.168.1.129:3306/gruul-addon-consumption-rebate",
                        "root",
                        "public2022"
                )
                .globalConfig(builder -> {
                    builder.author("WuDi")
                            //.enableSwagger()
                            //.fileOverride()
                            .outputDir("D:\\e\\code\\5.启山\\coding-mid-platform\\addon-consumption-rebate\\src\\main\\java");
                })
                .packageConfig(builder -> {
                    builder.parent("com.medusa.gruul.addon.consumption.rebate")
                            .moduleName("mp")
                            .pathInfo(
                                    new HashMap<OutputFile, String>() {
                                        private static final long serialVersionUID = 4195354013409294015L;

                                        {
                                            put(OutputFile.xml, "D:\\e\\code\\5.启山\\coding-mid-platform\\addon-consumption-rebate\\src\\main\\resources\\mapper");
                                        }
                                    }
                            );
                })
                .strategyConfig(builder -> {
                    builder.addInclude("t_rebate_payment")
                            .addTablePrefix("t_")
                            .entityBuilder()
                            .enableLombok()
                            .superClass(BaseEntity.class);
                }).execute();
    }

    @Test
    public void test() {
//        new RebateOrder()
    }

}
