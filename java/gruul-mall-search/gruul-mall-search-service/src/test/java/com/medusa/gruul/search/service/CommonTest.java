package com.medusa.gruul.search.service;

import com.medusa.gruul.search.api.model.ProductVO;
import com.medusa.gruul.search.api.rpc.SearchRpcService;
import com.medusa.gruul.search.service.es.entity.EsProductEntity;
import com.medusa.gruul.search.service.es.mapper.EsProductActivityMapper;
import com.medusa.gruul.search.service.model.SearchParam;
import com.medusa.gruul.search.service.service.EsProductService;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author 张治保
 * @since 2023/9/23
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CommonTest {

    @Autowired
    private SearchRpcService searchRpcService;

    @Autowired
    private EsProductService esProductService;

    @Autowired
    private EsProductActivityMapper productActivityMapper;

    @Test
    public void test() {
        Map<Long, List<ProductVO>> shopProductMap = searchRpcService.salesRanking(
                Set.of(
                        1765293654351069184L,
                        1568136320883163136L
                ),
                1
        );
        shopProductMap.values()
                .forEach(System.out::println);
    }

    /**
     * 1765293654351069184:1765294804168503296
     * 1765293654351069184:1768182386460094464
     */
    @Test
    public void testSortedByParam() {
        EsPageInfo<EsProductEntity> search = esProductService.search(
                false,
                new SearchParam()
                        .setIds(
                                List.of(
                                        "1765293654351069184:1768182386460094464",
                                        "1765293654351069184:1765294804168503296"
                                )
                        )
        );
    }
}
