package com.medusa.gruul.carrier.pigeon.service.modules.oss;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrPool;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.upload.UploadPretreatment;

import java.time.LocalDate;

/**
 * @author 张治保
 * @since 2024/3/16
 */
public interface FileHelper {


    /**
     * 文件存储路径 eg.2024/3/16
     * @return 文件存储路径
     */
    static String filePath(){
        LocalDate now = LocalDate.now();
        return now.getYear() + StrPool.SLASH + now.getMonthValue() + StrPool.SLASH + now.getDayOfMonth();
    }

    /**
     * 设置路径 并 上传文件 
     * @param statement 文件上传前置处理
     * @return 文件信息
     */
    static FileInfo uploadWithPath(UploadPretreatment statement){
        return statement.setPath(filePath())
                .upload();
    } 
}
