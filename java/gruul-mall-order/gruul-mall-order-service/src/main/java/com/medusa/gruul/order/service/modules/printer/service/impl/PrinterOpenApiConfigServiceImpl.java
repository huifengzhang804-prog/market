package com.medusa.gruul.order.service.modules.printer.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.order.service.model.enums.OrderError;
import com.medusa.gruul.order.service.modules.printer.feie.api.FeieApi;
import com.medusa.gruul.order.service.modules.printer.feie.api.FeieConfig;
import com.medusa.gruul.order.service.modules.printer.feie.api.FeieResponse;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.PrinterInfoResp;
import com.medusa.gruul.order.service.modules.printer.model.PrinterConstant;
import com.medusa.gruul.order.service.modules.printer.model.dto.FeieConfigDTO;
import com.medusa.gruul.order.service.modules.printer.service.PrinterOpenApiConfigService;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * @since 2024/8/22
 */
@Service
@RequiredArgsConstructor
public class PrinterOpenApiConfigServiceImpl implements PrinterOpenApiConfigService {

    @Override
    @Redisson(name = PrinterConstant.FEIE_CONFIG_CACHE_KEY + "_LOCK")
    public void feie(FeieConfigDTO config) {
        //如果已经存在 则不允许再次修改或添加
        Boolean existed = RedisUtil.getRedisTemplate()
                .hasKey(PrinterConstant.FEIE_CONFIG_CACHE_KEY);
        if (BooleanUtil.isTrue(existed)) {
            throw SystemCode.DATA_EXISTED.exception();
        }
        FeieConfig feieConfig = new FeieConfig()
                .setUser(config.getUser())
                .setUkey(config.getUkey());
        //先测试配置是否正确
        //使用查询打印机信息的接口测试
        FeieResponse<PrinterInfoResp> response = new FeieApi(feieConfig).printerInfo("null-undefined");
        //以下两种情况代表飞鹅开放平台 user 和 ukey 配置错误
        // user 不正确
        // 1. FeieResponse(ret=-2, msg=参数错误 : 该帐号未注册., data=null, serverExecutedTime=2)
        // ukey 不正确
        // 2. FeieResponse(ret=-3, msg=验证失败 : 签名错误., data=null, serverExecutedTime=3)
        Integer code = response.getRet();
        String msg = response.getMsg();
        if (
                (code.equals(-2) && msg.contains("该帐号未注册"))
                        ||
                        (response.getRet().equals(-3) && msg.contains("签名错误"))
        ) {
            throw OrderError.PRINTER_WRONG_OPEN_CONFIG.exception();
        }
        RedisUtil.setCacheMap(PrinterConstant.FEIE_CONFIG_CACHE_KEY, feieConfig);
    }

    @Override
    public FeieConfig feieConfig() {
        return Option.of(RedisUtil.getCacheMap(PrinterConstant.FEIE_CONFIG_CACHE_KEY, FeieConfig.class))
                .getOrElse(FeieConfig::new);
    }
}
