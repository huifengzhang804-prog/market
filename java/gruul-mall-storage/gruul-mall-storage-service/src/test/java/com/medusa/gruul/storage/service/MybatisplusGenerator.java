package com.medusa.gruul.storage.service;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
//import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBo1otTest;

import java.util.HashMap;

/**
 * @author 张治保
 * date 2022/2/24
 */
//@SpringBootTest
public class MybatisplusGenerator {

    public static void main(String[] args) {
        FastAutoGenerator.create(
            "jdbc:mysql://124.222.25.145:3305/gruul-storage",
            "root",
            "public2020"
        )
            .globalConfig(builder -> {
                builder.author("张治保")
                    //.enableSwagger()
                    .outputDir("/Users/yusi/Documents/project/zhongtai/gruul-mall-storage/gruul-mall-storage-service/src/main/java");
            })
            .packageConfig(builder -> {
                builder.parent("com.medusa.gruul.storage.service")
                    .moduleName("mp")
                    .pathInfo(
                        new HashMap<OutputFile, String>(){
                            {
                                put(OutputFile.xml,"/Users/yusi/Documents/project/zhongtai/gruul-mall-storage/gruul-mall-storage-service/src/main/resources/mapper");
                            }
                        }
                    );
            })
            .strategyConfig(builder -> {
                builder.addInclude("t_storage_spec_group")
                    .addTablePrefix("t_")
                    .entityBuilder()
                    .enableLombok()
                .superClass(BaseEntity.class);
            }).execute();
    }

    @Autowired
    StorageRpcService storageRpcService;
//    @Test
    public void test1() {
//        List<StorageSku> productSkuByProductId = storageRpcService.getProductSkuByProductId(Arrays.asList(1550016098857807872L, 1549990698672877568L));
//        System.out.println("productSkuByProductId = " + productSkuByProductId);
    }
}
