package test;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.medusa.gruul.common.mp.model.BaseEntity;

import java.util.HashMap;

/**
 * @author 张治保
 * date 2022/2/24
 */
public class MybatisplusGenerator {

    public static void main(String[] args) {
        FastAutoGenerator.create(
            "jdbc:mysql://121.4.98.245:3305/gruul-addon-platform",
            "root",
            "public2020"
        )
            .globalConfig(builder -> {
                builder.author("张治保")
                    //.enableSwagger()
                    //.fileOverride()
                    .outputDir("E:\\mall-framework\\gruul-mall-addon-platform\\src\\main\\java");
            })
            .packageConfig(builder -> {
                builder.parent("com.medusa.gruul.addon.platform")
                    .moduleName("mp")
                    .pathInfo(
                        new HashMap<OutputFile, String>(){
                            private static final long serialVersionUID = 4195354013431294015L;
                            {
//                                put(OutputFile.mapperXml,"E:\\mall-framework\\gruul-mall-addon-platform\\src\\main\\resources\\mapper");
                            }
                        }
                    );
            })
            .strategyConfig(builder -> {
                builder.addInclude("t_platform_shop_register_info")
                    .addTablePrefix("t_")
                    .entityBuilder()
                    .enableLombok()
                .superClass(BaseEntity.class);
            }).execute();
    }
}
