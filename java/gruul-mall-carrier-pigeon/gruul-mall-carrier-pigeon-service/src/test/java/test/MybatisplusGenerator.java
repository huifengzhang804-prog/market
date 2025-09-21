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
            "jdbc:mysql://192.168.1.129:3306/gruul-carrier-pigeon",
            "root",
            "public2022"
        )
            .globalConfig(builder -> {
                builder.author("张治保")
                    //.enableSwagger()
                    //.fileOverride()
                    .outputDir("/Users/yusi/Documents/project/zhongtai/gruul-mall-carrier-pigeon/gruul-mall-carrier-pigeon-service/src/main/java");
            })
            .packageConfig(builder -> {
                builder.parent("com.medusa.gruul.carrier.pigeon.service")
                    .moduleName("mp")
                    .pathInfo(
                        new HashMap<OutputFile, String>(){
                            private static final long serialVersionUID = 4195354013431294015L;
                            {
                                put(OutputFile.xml,"/Users/yusi/Documents/project/zhongtai/gruul-mall-carrier-pigeon/gruul-mall-carrier-pigeon-service/src/main/resources/mapper");
                            }
                        }
                    );
            })
            .strategyConfig(builder -> {
                builder.addInclude("t_message_shop_admin")
                    .addTablePrefix("t_")
                    .entityBuilder()
                    .enableLombok()
                .superClass(BaseEntity.class);
            }).execute();
    }
}
