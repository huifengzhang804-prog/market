package com.medusa.gruul.addon.ic.modules.opens.judanke;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.PropertyNamingStrategy;
import com.alibaba.fastjson2.filter.Filter;
import com.alibaba.fastjson2.filter.NameFilter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author 张治保
 * @since 2024/8/5
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class JudankeRequest implements Serializable {

    private static volatile Filter namingFilter;

    /**
     * app_id	string	是	 用户ID（注册开放平台时分配，在控制台中查看）
     */
    private String app_id;

    /**
     * 当前请求的时间戳，精确到毫秒，位数13位
     */
    private String ts;
    /**
     * 随机字符串，最大长度32位
     */
    private String nonce;

    /**
     * 按照规则(sha1(ts + app_key + api + app_id + nonce))生成的合法性验证签名(40位字符串，字母小写)。
     * 如：
     * ts: 1668494922561
     * app_key: 39936e011ade195c154b1ed709c7cbd9f099ea61
     * api: /Other/getAllDeliveryBrand
     * app_id: 100100
     * nonce: ghod8141m7h
     * 加密后 e053c9a216916de4a1a4ba9f5a1c04d408accd925
     */
    private String sign;

    /**
     * JSON格式业务请求参数
     */
    private String data;

    /**
     * 聚单客请求静态构造工具
     *
     * @param userId 开发者用户 id
     * @param apiKey 开发者 api key
     * @param api    请求的 uri
     * @param data   请求数据
     * @return 聚单客请求数据
     */
    public static JudankeRequest of(String userId, String apiKey, String api, Object data) {
        String ts = Long.toString(System.currentTimeMillis());
        String nonce = getNonce(10 + ThreadLocalRandom.current().nextInt(10));
        JudankeRequest request = new JudankeRequest()
                .setApp_id(userId)
                .setTs(ts)
                .setNonce(nonce)
                .setSign(sha1(ts + apiKey + api + userId + nonce));
        if (data == null) {
            request.setData("{}");
        } else {
            request.setData(
                    data instanceof String
                            ? (String) data
                            : JSON.toJSONString(data, getFilter(), JSONWriter.Feature.WriteBigDecimalAsPlain));
        }

        return request;
    }


    /**
     * 计算Sha1 加密
     *
     * @param str 加密原始字符串
     * @return 加密后的结果
     */
    private static String sha1(String str) {
        try {
            // 获取MD5实例
            MessageDigest md = MessageDigest.getInstance("SHA1");
            // 此处传入要加密的byte类型值
            md.update(str.getBytes(StandardCharsets.UTF_8));
            // 此处得到的是md5加密后的byte类型值
            byte[] digest = md.digest();

            return toHexString(digest).substring(0, 40);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getNonce(int size) {
        byte[] array = new byte[size];
        ThreadLocalRandom.current().nextBytes(array);
        return toHexString(array);
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 可以不加锁 无非并发是重新创建几次 然后就可以稳定运行
     */
    public static Filter getFilter() {
        if (namingFilter != null) {
            return namingFilter;
        }
        namingFilter = NameFilter.of(PropertyNamingStrategy.SnakeCase);
        return namingFilter;
    }
}
