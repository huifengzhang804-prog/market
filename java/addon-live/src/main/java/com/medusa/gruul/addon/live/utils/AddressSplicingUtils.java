package com.medusa.gruul.addon.live.utils;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.addon.live.constant.LiveConstants;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author miskw
 * @date 2023/6/2
 * @describe 地址拼接
 */
@Data
public class AddressSplicingUtils {

    private static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /*
     * 获取
     * KEY+ streamName + txTime
     */
    public static String getSafeUrl(String key, String streamName, long txTime, String domain, Boolean isPush) {
        String input = key +
                streamName +
                Long.toHexString(txTime).toUpperCase();

        String txSecret;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            txSecret = byteArrayToHexString(
                    messageDigest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "推拉流地址获取失败!");
        }
        if (isPush) {
            return StrUtil.format(LiveConstants.CONCATENATED_ADDRESS, LiveConstants.RTMP + domain + streamName + "?", txSecret, Long.toHexString(txTime).toUpperCase());
        }
        //前端根据逗号切割获取对应拉流地址
        return handlerPullAddress(domain, streamName, txSecret, Long.toHexString(txTime).toUpperCase());
    }

    private static String handlerPullAddress(String domain, String streamName, String txSecret, String txTime) {
        //RTMP地址
        String rtmpPull = StrUtil.format(LiveConstants.CONCATENATED_ADDRESS, LiveConstants.RTMP + domain + streamName + "?", txSecret, txTime);
        //FLV地址
        String fLVPull = StrUtil.format(LiveConstants.CONCATENATED_ADDRESS, LiveConstants.HTTP + domain + streamName +".flv"+ "?", txSecret, txTime);
        //HLS地址
        String hlsPull = StrUtil.format(LiveConstants.CONCATENATED_ADDRESS, LiveConstants.HTTP + domain + streamName +".m3u8"+ "?", txSecret, txTime);
        //WebRTC地址
        String webRTCPull = StrUtil.format(LiveConstants.CONCATENATED_ADDRESS, LiveConstants.WEBRTC + domain + streamName + "?", txSecret, txTime);
        return StrUtil.join(StrUtil.COMMA,rtmpPull, fLVPull, hlsPull, webRTCPull);
    }

    private static String byteArrayToHexString(byte[] data) {
        char[] out = new char[data.length << 1];

        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }

    public static String getStreamName(LocalDateTime dateTime) {
        String format = LocalDateTimeUtil.format(dateTime, DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        return StrUtil.concat(true, format, UUID.fastUUID().toString());
    }
}
