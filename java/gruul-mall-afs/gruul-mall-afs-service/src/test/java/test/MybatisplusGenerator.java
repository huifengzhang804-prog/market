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
            "jdbc:mysql://121.4.98.245:3305/gruul-afs",
            "root",
            "public2020"
        )
            .globalConfig(builder -> {
                builder.author("张治保")
                    //.enableSwagger()
                    .outputDir("/Users/yusi/Documents/project/zhongtai/gruul-mall-afs/gruul-mall-afs-service/src/main/java");
            })
            .packageConfig(builder -> {
                builder.parent("com.medusa.gruul.afs.api")
                    .moduleName("mp")
                    .pathInfo(
                        new HashMap<OutputFile, String>(){
                            {
                                put(OutputFile.xml,"/Users/yusi/Documents/project/zhongtai/gruul-mall-afs/gruul-mall-afs-service/src/main/resources/mapper");
                            }
                        }
                    );
            })
            .strategyConfig(builder -> {
                builder.addInclude("t_afs_order","t_afs_package","t_afs_history","t_afs_order_item")
                    .addTablePrefix("t_")
                    .entityBuilder()
                    .enableLombok()
                .superClass(BaseEntity.class);
            }).execute();
    }
}
