package com.medusa.gruul.service.uaa.service.service.impl;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.medusa.gruul.service.uaa.service.model.dto.scancode.RedirectQrCodeDTO;
import com.medusa.gruul.service.uaa.service.properties.UaaProperties;
import com.medusa.gruul.service.uaa.service.service.ScanCodeService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * date 2023/5/10
 */
@Service
@RequiredArgsConstructor
public class ScanCodeServiceImpl implements ScanCodeService {

    private static final String REDIRECT_QUERY_NAME = "codeRedirect";
    private static final String HASH_PLACEHOLDER = "/hsplchd1/2hsplchd";
    private final UaaProperties uaaProperties;

    @SneakyThrows
    @Override
    public String redirectQrcode(RedirectQrCodeDTO redirect) {
        UrlBuilder urlBuilder = UrlBuilder.of(uaaProperties.getH5Url());
        if (redirect.getHash()) {
            urlBuilder.addPath(HASH_PLACEHOLDER);
        }
        //添加路径
        if (StrUtil.isNotBlank(redirect.getPath())) {
            urlBuilder.addPath(redirect.getPath());
        }
        //添加参数
        if (redirect.getParams() != null) {
            urlBuilder.setQuery(UrlQuery.of(redirect.getParams(), true));
        }
        //用于表示 扫码需要跳转
        if (redirect.getCodeRedirect()) {
            urlBuilder.addQuery(REDIRECT_QUERY_NAME, true);
        }
        return QrCodeUtil.generateAsBase64(
                redirect.getHash() ? urlBuilder.build().replace(HASH_PLACEHOLDER, "/#") : urlBuilder.build(),
                QrConfig.create(),
                ImgUtil.IMAGE_TYPE_PNG
        );
    }


}
