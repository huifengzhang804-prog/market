package com.medusa.gruul.payment.service.common.helper;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;


/**
 * 微信证书文件辅助类
 *
 * @author 张治保
 */
@Slf4j
public class WechatHelper {

    public static final Set<String> SUFFIX_LIST = Set.of("p12", "crt", "pem");
    private static final String FILE_PATH_TEMPLATE = "{}/data/pay-certificate/{}";

    /**
     * 校验证书文件名
     */
    public static boolean nameVerify(String fileName) {
        return StrUtil.isNotBlank(fileName) &&
                SUFFIX_LIST.contains(FileUtil.getSuffix(fileName));
    }

    /**
     * 获取证书文件绝对路径
     */
    public static String absolutePath(String tenantId, String originalFilename) {
        String folder = StrUtil.format(FILE_PATH_TEMPLATE, (FileUtil.isWindows() ? "C:" : ""), tenantId);
        FileUtil.mkdir(folder);

        return folder.concat(
                IdUtil.simpleUUID().concat(".").concat(FileNameUtil.extName(originalFilename))
        );
    }
}
