package com.medusa.gruul.order.service.modules.printer.feie.api;

import cn.hutool.core.text.StrPool;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.medusa.gruul.global.model.helper.request.IRequest;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.*;
import com.medusa.gruul.order.service.modules.printer.feie.api.model.enums.FeiePrinterStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/8/14
 */
public interface IFeieApi {

    /**
     * log
     */
    Logger log = LoggerFactory.getLogger(IFeieApi.class);

    /**
     * 请求参数 转 url 参数拼接的形式
     *
     * @param param 请求参数
     * @return url 拼接参数
     */
    private static String urlEncode(Map<String, Object> param) {
        if (param == null || param.isEmpty()) {
            return "";
        }
        Set<Map.Entry<String, Object>> entries = param.entrySet();
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Object> entry : entries) {
            if (!result.isEmpty()) {
                result.append("&");
            }
            String name = entry.getKey();
            if (name == null || name.isEmpty()) {
                continue;
            }
            result.append(URLEncoder.encode(name, StandardCharsets.UTF_8))
                    .append("=");
            Object value = entry.getValue();
            if (value == null) {
                continue;
            }
            result.append(URLEncoder.encode(value instanceof String a ? a : value.toString(), StandardCharsets.UTF_8));
        }
        return result.toString();
    }

    /**
     * 获取飞鹅配置信息
     *
     * @return 飞鹅配置信息
     */
    FeieConfig config();

    /**
     * 发送请求至 飞鹅开放平台
     *
     * @param apiName   请求的接口名称
     * @param data      请求数据
     * @param reference 相应数据类型
     * @param <T>       响应数据类型
     * @return 请求结果
     */
    default <T> FeieResponse<T> request(String apiName, Map<String, Object> data, TypeReference<FeieResponse<T>> reference) {
        FeieConfig config = config();
        JSONObject request = FeieRequest.of(config.getUser(), config.getUkey(), apiName, config.isTest());

        request.putAll(data);
        String requestBody = urlEncode(request);
        log.debug("--==>feie api:{}  requestBody: \n{}", apiName, requestBody);
        String responseStr = IRequest.INSTANCE
                .DEFAULT
                .post(
                        config.domain(),
                        Map.of(
                                "Content-Type", "application/x-www-form-urlencoded"
                        ),
                        requestBody
                );
        log.debug("--==>feie api:{}  response: \n{}", apiName, responseStr);
        return JSON.parseObject(responseStr, reference);
    }

    /**
     * 批量添加打印机
     * <a href="https://help.feieyun.com/home/doc/zh;nav=1-0">批量添加打印机</a>
     *
     * @param param 打印机信息列表
     * @return 打印机添加结果
     */
    default FeieResponse<BatchAddPrinterResp> batchAddPrinter(List<AddPrinterParam> param) {
        return request(
                "Open_printerAddlist",
                Map.of("printerContent", AddPrinterParam.batchFormat(param)),
                new TypeReference<>() {
                }
        );
    }


    /**
     * 批量删除打印机，Open_printerDelList
     * <a href="https://help.feieyun.com/home/doc/zh;nav=1-3">批量删除打印机</a>
     *
     * @param sns 打印机sn列表
     * @return 删除结果
     */
    default FeieResponse<BatchAddPrinterResp> batchDeletePrinter(Set<String> sns) {
        return request(
                "Open_printerDelList",
                Map.of("snlist", String.join(StrPool.DASHED, sns)),
                new TypeReference<>() {
                }
        );
    }


    /**
     * 修改打印机信息，Open_printerEdit
     * <a href="https://help.feieyun.com/home/doc/zh;nav=1-4">修改打印机信息</a>
     *
     * @param param 修改打印机数据
     * @return 修改结果
     */
    default FeieResponse<Boolean> printerEdit(PrinterEditParam param) {
        return request(
                "Open_printerEdit",
                JSONObject.from(param),
                new TypeReference<>() {
                }
        );
    }


    /**
     * 清空待打印队列，Open_delPrinterSqs
     * <a href="https://help.feieyun.com/home/doc/zh;nav=1-5">清空待打印队列</a>
     *
     * @param sn 打印机 sn 序列号
     * @return 清空结果
     */
    default FeieResponse<Boolean> printerEdit(String sn) {
        return request(
                "Open_delPrinterSqs",
                Map.of("sn", sn),
                new TypeReference<>() {
                }
        );
    }


    /**
     * 小票机打印订单，Open_printMsg
     * <a href="https://help.feieyun.com/home/doc/zh;nav=1-1">小票机打印订单</a>
     *
     * @param param 打印参数
     * @return 打印订单 id
     */
    default FeieResponse<String> printMsg(PrintMsgParam param) {
        return request(
                "Open_printMsg",
                JSONObject.from(param),
                new TypeReference<>() {
                }
        );
    }


    /**
     * 查询订单是否打印成功，Open_queryOrderState
     * <a href="https://help.feieyun.com/home/doc/zh;nav=1-6">查询订单是否打印成功</a>
     *
     * @param orderid 订单 id 订单ID，由接口小票机打印订单（Open_printMsg）返回。
     * @return 是否打印成功
     */
    default FeieResponse<Boolean> queryOrderState(String orderid) {
        return request(
                "Open_queryOrderState",
                Map.of("orderid", orderid),
                new TypeReference<>() {
                }
        );
    }


    /**
     * 查询指定打印机某天的订单统计数，Open_queryOrderInfoByDate
     * <a href="https://help.feieyun.com/home/doc/zh;nav=1-8">查询指定打印机某天的订单统计数</a>
     *
     * @param param 打印机 sn 与查询日期
     * @return 是否打印成功
     */
    default FeieResponse<QueryOrderInfoByDateResp> queryOrderInfoByDate(QueryOrderInfoByDateParam param) {
        return request(
                "Open_queryOrderInfoByDate",
                JSONObject.from(param),
                new TypeReference<>() {
                }
        );
    }

    /**
     * 获取某台打印机状态，Open_queryPrinterStatus
     * <a href="https://help.feieyun.com/home/doc/zh;nav=1-9">查询指定打印机状态</a>
     *
     * @param sn 打印机 sn  码
     * @return 是否打印成功
     */
    default FeieResponse<FeiePrinterStatus> queryPrinterStatus(String sn) {
        return request(
                "Open_queryPrinterStatus",
                Map.of("sn", sn),
                new TypeReference<>() {
                }
        );
    }

    /**
     * 获取指定打印机信息，Open_printerInfo
     * <a href="https://help.feieyun.com/home/doc/zh;nav=1-10">获取指定打印机信息</a>
     *
     * @param sn 打印机 sn  码
     * @return 是否打印成功
     */
    default FeieResponse<PrinterInfoResp> printerInfo(String sn) {
        return request(
                "Open_printerInfo",
                Map.of("sn", sn),
                new TypeReference<>() {
                }
        );
    }


}
