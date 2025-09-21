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
                        "jdbc:mysql://192.168.1.129:3306/gruul-overview",
                        "root",
                        "public2022"
                )
                .globalConfig(builder -> {
                    builder.author("WuDi")
                            //.enableSwagger()
                            .outputDir("/Users/yusi/Documents/zhongtai/gruul-mall-overview/gruul-mall-overview-service/src/main/java");
                })
                .packageConfig(builder -> {
                    builder.parent("com.medusa.gruul.overview.service")
                            .moduleName("mp")
                            .pathInfo(
                                    new HashMap<OutputFile, String>() {
                                        {
                                            put(OutputFile.xml, "/Users/yusi/Documents/zhongtai/gruul-mall-overview/gruul-mall-overview-service/src/main/resources/mapper");
                                        }
                                    }
                            );
                })
                .strategyConfig(builder -> {
                    builder.addInclude("t_overview_distributor")
                            .addTablePrefix("t_")
                            .entityBuilder()
                            .enableLombok()
                            .superClass(BaseEntity.class);
                }).execute();
    }
}
