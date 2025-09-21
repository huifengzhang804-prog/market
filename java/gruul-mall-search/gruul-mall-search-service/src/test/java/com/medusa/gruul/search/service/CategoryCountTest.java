package com.medusa.gruul.search.service;

import com.medusa.gruul.search.api.enums.CategoryCountType;
import com.medusa.gruul.search.api.model.CategoryCountParam;
import com.medusa.gruul.search.service.service.EsProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/3/18
 */


@SpringBootTest
@RunWith(SpringRunner.class)
public class CategoryCountTest {


    @Autowired
    EsProductService esProductService;

    @Test
    public void test() {

//        Map<Long, Long> categoryCountMap = esProductService.categoryCount(
//                new CategoryCountParam()
//                        .setType(CategoryCountType.PLATFORM)
//                        .setFirstIds(Set.of(1567794194818166784L))
//        );
//        System.out.println(categoryCountMap);
    }
}
