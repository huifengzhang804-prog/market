package com.medusa.gruul.overview.service;

import com.medusa.gruul.overview.service.mp.export.service.IDataExportRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Service 测试类
 *
 * @author jipeng
 * date 2024/2/2
 */
@Slf4j
@SpringBootTest
public class ServiceTest {

    @Resource
    private IDataExportRecordService dataExportRecordService;

    @Test
    public void test1() {
        var list = dataExportRecordService.lambdaQuery().list();
        log.info("list:{}", list);
    }
}
