package generator;

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
                        "jdbc:mysql://121.4.98.245:3305/gruul-user",
                        "root",
                        "public2020"
                )
                .globalConfig(builder -> {
                    builder.author("张治保")
                            .enableSwagger()
                            .outputDir("E:\\mall-framework\\gruul-mall-user\\gruul-mall-user-service\\src\\main\\java");
                })
                .packageConfig(builder -> {
                    builder.parent("com.medusa.gruul.user.service")
                            .moduleName("mp")
                            .pathInfo(
                                    new HashMap<OutputFile, String>() {
                                        {
                                            put(OutputFile.xml, "D:\\e\\code\\5.启山\\coding-mid-platform\\gruul-mall-user\\gruul-mall-user-service\\src\\main\\resources\\mapper");
                                            put(OutputFile.entity, "D:\\e\\code\\5.启山\\coding-mid-platform\\gruul-mall-user\\gruul-mall-user-service\\src\\main\\java\\com\\medusa\\gruul\\user\\service\\mp\\entity");
                                        }
                                    }
                            );
                })
                .strategyConfig(builder -> {
                    builder.addInclude("t_shop_user_account")
                            .addTablePrefix("t_")
                            .entityBuilder()
                            .enableLombok()
                            .superClass(BaseEntity.class);
                }).execute();
    }
}
