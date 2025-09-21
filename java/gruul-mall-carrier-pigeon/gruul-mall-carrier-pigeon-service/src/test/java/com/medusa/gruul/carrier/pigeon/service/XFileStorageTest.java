package com.medusa.gruul.carrier.pigeon.service;

import cn.hutool.core.date.DateUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Date;

/**
 * @author jipeng
 * @since 2024/2/23
 */
@Slf4j
//@RequiredArgsConstructor
@SpringBootTest
public class XFileStorageTest {

    @Resource
    FileStorageService fileStorageService;

    @Test
    public void testUploadFile() {
        File file = new File("C:\\Users\\Administrator\\Desktop\\imgs\\25.jpg");
        FileInfo fileInfo = fileStorageService.of(file)
                .setPath(DateUtil.format(new Date(), "yyyy/MM/dd/")).upload();
        log.debug("：fileInfo={}", fileInfo);

    }

}
