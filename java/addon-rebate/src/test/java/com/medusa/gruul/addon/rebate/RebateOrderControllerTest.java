package com.medusa.gruul.addon.rebate;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.rebate.model.dto.RebateOrderQueryDTO;
import com.medusa.gruul.addon.rebate.mp.entity.RebateOrder;
import com.medusa.gruul.addon.rebate.service.RebateOrderHandlerService;
import com.medusa.gruul.common.model.resp.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.medusa.gruul.common.model.resp.Result.ok;

/**
 * @author: wufuzhong
 * @Date: 2023/10/13 09:14:14
 * @Description：返利订单测试类
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class RebateOrderControllerTest {

    @Autowired
    private RebateOrderHandlerService rebateOrderHandlerService;

    /**
     * 分页查询消费返利订单
     */
    @Test
    public void pageRebateOrderTest(){
        RebateOrderQueryDTO rebateOrderQuery = new RebateOrderQueryDTO();
        rebateOrderQuery.setCurrent(1L);
        rebateOrderQuery.setUserId(1L);
        Result<IPage<RebateOrder>> result = Result.ok(
                rebateOrderHandlerService.pageRebateOrder(rebateOrderQuery)
        );
        System.out.println(result);
    }
}
