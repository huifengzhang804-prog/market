import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.medusa.gruul.common.mp.model.BaseEntity;

import java.util.HashMap;

public class MybatisplusGenerator {

    public static void main(String[] args) {
        FastAutoGenerator.create(
                        "jdbc:mysql://192.168.1.129:3306/gruul-addon-bargain",
                        "root",
                        "public2022"
                )
                .globalConfig(builder -> {
                    builder.author("WuDi")
                            //.enableSwagger()
                            //.fileOverride()
                            .outputDir("D:\\e\\code\\5.启山\\coding-mid-platform\\addon-bargain\\addon-bargain\\src\\main\\java");
                })
                .packageConfig(builder -> {
                    builder.parent("com.medusa.gruul.addon.bargain")
                            .moduleName("mp")
                            .pathInfo(
                                    new HashMap<OutputFile, String>() {
                                        private static final long serialVersionUID = 4195354013431294015L;

                                        {
                                            put(OutputFile.xml, "D:\\e\\code\\5.启山\\coding-mid-platform\\addon-bargain\\addon-bargain\\src\\main\\resources\\mapper");
                                        }
                                    }
                            );
                })
                .strategyConfig(builder -> {
                    builder.addInclude("t_bargain_payment_info")
                            .addTablePrefix("t_")
                            .entityBuilder()
                            .enableLombok()
                            .superClass(BaseEntity.class);
                }).execute();
    }
}
