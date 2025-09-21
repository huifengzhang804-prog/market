package com.medusa.gruul.order.service.modules.printer.feie.api;

import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 飞鹅公共请求参数模型
 *
 * @author 张治保
 * @since 2024/8/14
 */
@Getter
@Setter
@ToString
public class FeieRequest {

    /**
     * user 	必须	string	飞鹅云后台注册用户名。
     */
    private String user;

    /**
     * stime 	必须	string	当前UNIX时间戳，10位，精确到秒。
     */
    private String stime;

    /**
     * sig 	必须	string	对参数user+UKEY+stime 拼接后（+号表示连接符）进行SHA1加密得到签名，加密后签名值为40位小写字符串。
     */
    private String sig;

    /**
     * apiname 	必须	string	请求的接口名称。
     */
    private String apiname;

    /**
     * 这个可以忽略 没用
     * debug 		string	debug=1返回非json格式的数据。仅测试时候使用。
     */
    private String debug;


    /**
     * 公共请求数据格式
     *
     * @param user    飞鹅云后台注册用户名。
     * @param ukey    飞鹅云后台注册UKEY
     * @param apiName 请求接口名称
     * @param isTest  是否是测试环境
     * @return 公共请求数据
     */
    public static JSONObject of(String user, String ukey, String apiName, boolean isTest) {
        String stime = Long.toString(System.currentTimeMillis() / 1000);
        JSONObject result = JSONObject.of()
                .fluentPut("user", user)
                .fluentPut("stime", stime)
                .fluentPut("sig", sha1(user + ukey + stime).toLowerCase())
                .fluentPut("apiname", apiName);

        if (isTest) {
            result.put("debug", "1");
        }
        return result;
    }

    /**
     * 计算Sha1 加密
     *
     * @param str 加密原始字符串
     * @return 加密后的结果
     */
    private static String sha1(String str) {
        MessageDigest md;
        try {
            // 获取MD5实例
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new GlobalException("SHA1 Algorithm not found", e);
        }
        // 此处传入要加密的byte类型值
        md.update(str.getBytes(StandardCharsets.UTF_8));
        // 此处得到的是md5加密后的byte类型值
        byte[] digest = md.digest();
        return toHexString(digest).substring(0, 40);
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
