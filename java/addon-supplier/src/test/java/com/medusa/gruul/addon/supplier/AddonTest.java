package com.medusa.gruul.addon.supplier;

import com.medusa.gruul.addon.supplier.addon.SupplierLogisticsAddonSupport;
import com.medusa.gruul.common.model.enums.DistributionMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author 张治保
 * date 2023/7/26
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AddonTest {

    @Autowired
    private SupplierLogisticsAddonSupport supplierLogisticsAddonSupport;

    @Test
    public void test() {
        Map<String, BigDecimal> result = supplierLogisticsAddonSupport.distributionCost(DistributionMode.EXPRESS, null);
        System.out.println(result);
    }

}
