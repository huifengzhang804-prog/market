package com.medusa.gruul.global.model.helper.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * @author 张治保
 * @since 2024/8/10
 */
@Slf4j
@RequiredArgsConstructor
public class Request implements IRequest {

    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
    }};
    private final int readTimeOut;

    private static void trustAllHosts(HttpsURLConnection connection) {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();

            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        connection.setHostnameVerifier((hostname, session) -> true);
    }

    @Override
    public String post(String url, Map<String, String> headers, String body) {
        HttpURLConnection connection = null;
        try {
            // connection
            connection = (HttpURLConnection) new URL(url).openConnection();
            // trust-https
            if (url.startsWith("https")) {
                trustAllHosts((HttpsURLConnection) connection);
            }
            // connection setting
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(readTimeOut);
            connection.setConnectTimeout(3 * 1000);
            if (headers != null && !headers.isEmpty()) {
                headers.forEach(connection::setRequestProperty);
            }
            // do connection
            connection.connect();

            // write requestBody
            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(body.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }
            // valid StatusCode
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("request fail, StatusCode(" + statusCode + ") invalid. for url : " + url + "[" + connection.getResponseMessage() + "]");
            }
            // result
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("request error(" + e.getMessage() + "), for url : " + url, e);
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e2) {
                log.error(e2.getMessage(), e2);
            }
        }
    }
}
