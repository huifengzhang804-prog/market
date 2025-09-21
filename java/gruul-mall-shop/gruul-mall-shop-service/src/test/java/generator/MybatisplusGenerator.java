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
            "jdbc:mysql://124.222.25.145:3305/gruul-goods",
            "root",
            "public2020"
        )
            .globalConfig(builder -> {
                builder.author("WuDi")
                    //.enableSwagger()
                    //.fileOverride()
                    .outputDir("D:\\e\\code\\5.启山\\mid-platform\\Generator");
            })
            .packageConfig(builder -> {
                builder.parent("com.medusa.gruul.goods.service")
                    .moduleName("mp")
                    .pathInfo(
                        new HashMap<OutputFile, String>(){
                            {
                                put(OutputFile.xml,"D:\\e\\code\\5.启山\\mid-platform\\gruul-mall-goods\\gruul-mall-goods-service\\src\\main\\resources\\mapper");
                                put(OutputFile.entity,"D:\\e\\code\\5.启山\\mid-platform\\gruul-mall-goods\\gruul-mall-goods-api\\src\\main\\java\\com\\medusa\\gruul\\goods\\api\\entity");
                            }
                        }
                    );
            })
            .strategyConfig(builder -> {
                builder.addInclude("t_shop_follow_new_products")
                    .addTablePrefix("t_")
                    .entityBuilder()
                    .enableLombok()
                .superClass(BaseEntity.class);
            }).execute();
    }
}
