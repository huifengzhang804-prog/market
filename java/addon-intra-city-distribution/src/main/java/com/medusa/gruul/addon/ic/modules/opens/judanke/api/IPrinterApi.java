package com.medusa.gruul.addon.ic.modules.opens.judanke.api;

import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.addon.ic.modules.opens.judanke.JudankeResponse;
import com.medusa.gruul.addon.ic.modules.opens.judanke.model.printer.*;

/**
 * @author 张治保
 * @since 2024/8/6
 */
public interface IPrinterApi extends IBaseJudankeApi {

    /**
     * 绑定一台打印机，进行小票打印
     *
     * @param param 绑定参数
     * @return 绑定结果
     */

    default JudankeResponse<BindResp> bind(BindParam param) {
        return request(param, new TypeReference<>() {
        });
    }

    /**
     * 解绑打印机
     *
     * @param param 解绑参数
     * @return 解绑结果
     */
    default JudankeResponse<Void> unbind(UnbindParam param) {
        return request("unbind", param, null);
    }


    /**
     * 打印小票
     *
     * @param param 打印参数
     * @return 打印结果
     */
    default JudankeResponse<PrintsResp> prints(PrintsParam param) {
        return request("unbind", param, new TypeReference<>() {
        });
    }


    @Override
    default String apiPrefix() {
        return "/Printer/";
    }
}
