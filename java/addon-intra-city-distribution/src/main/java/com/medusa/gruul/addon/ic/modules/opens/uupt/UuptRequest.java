package com.medusa.gruul.addon.ic.modules.opens.uupt;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.medusa.gruul.addon.ic.modules.opens.uupt.model.order.OrderCallbackParam;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 张治保
 * @since 2024/8/10
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UuptRequest {


    /**
     * 是 用户openId
     */
    private String openId;

    /**
     * 是 时间戳（秒）
     */
    private Long timestamp;

    /**
     * json字符串 是  业务参数
     */
    private String biz;

    /**
     * 是 签名
     * 拼接biz的值+appkey的值+timestamp的值
     * 将拼接后的字符串进行MD5加密，加密后转16进制得到32位签名，然后转大写即可
     * 将得到的签名赋值给sign
     */
    private String sign;


    public static UuptRequest of(String appKey, String openId, Object data) {
        String biz = data == null ? "{}" : JSON.toJSONString(data, JSONWriter.Feature.WriteBigDecimalAsPlain);
        Long timestamp = System.currentTimeMillis() / 1000;
        return new UuptRequest()
                .setOpenId(openId)
                .setTimestamp(timestamp)
                .setBiz(biz)
                .setSign(signIt(biz, appKey, timestamp));
    }

    public static String signIt(String biz, String appKey, Long timestamp) {
        String input = biz + appKey + timestamp;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 回调验签 并返回回调数据
     *
     * @return 订单回调数据
     */
    public OrderCallbackParam signCheck(String appKey) {
        if (biz == null || biz.isEmpty() || timestamp == null || sign == null || sign.isEmpty()) {
            throw new SecurityException();
        }
        if (sign.equals(signIt(biz, appKey, timestamp))) {
            return JSON.parseObject(biz, OrderCallbackParam.class);
        }
        throw new SecurityException();
    }
}
