import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.medusa.gruul.common.mp.model.BaseEntity;

import java.util.HashMap;

public class MybatisplusGenerator {

    public static void main(String[] args) {
        FastAutoGenerator.create(
                        "jdbc:mysql://192.168.1.129:3306/gruul-addon-matching-treasure",
                        "root",
                        "public2022"
                )
                .globalConfig(builder -> {
                    builder.author("WuDi")
                            //.enableSwagger()
                            //.fileOverride()
                            .outputDir("D:\\e\\code\\5.启山\\coding-mid-platform\\addon-matching-treasure\\src\\main\\java");
                })
                .packageConfig(builder -> {
                    builder.parent("com.medusa.gruul.addon.matching.treasure")
                            .moduleName("mp")
                            .pathInfo(
                                    new HashMap<OutputFile, String>() {
                                        private static final long serialVersionUID = 94153789731294015L;

                                        {
                                            put(OutputFile.xml, "D:\\e\\code\\5.启山\\coding-mid-platform\\addon-matching-treasure\\src\\main\\resources\\mapper");
                                        }
                                    }
                            );
                })
                .strategyConfig(builder -> {
                    builder.addInclude("t_set_meal_payment_info")
                            .addTablePrefix("t_")
                            .entityBuilder()
                            .enableLombok()
                            .superClass(BaseEntity.class);
                }).execute();
    }
}
