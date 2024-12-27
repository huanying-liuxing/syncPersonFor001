package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileCopyUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;

@Slf4j
public class FileStreamUtils {
    /**
     * 得到文件流
     * @param url 图片地址
     * @return
     */
    public static byte[] getFileStream(String url){
        InputStream inStream = null;
        try {
            // 对 url中的中文进行编码
            //int index = url.lastIndexOf("/");
            // String u1 = url.substring(0, index);
            // String u2 = url.substring(index + 1);
            //url = u1 + "/" + URLEncoder.encode(u2, String.valueOf(StandardCharsets.UTF_8));
            URL httpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)httpUrl.openConnection();

            //跳过ssl证书验证
            HttpsURLConnection https = (HttpsURLConnection) conn;
            trustAllHosts(https);
            https.setHostnameVerifier(DO_NOT_VERIFY);

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(50000);
            conn.setReadTimeout(50000);
            //通过输入流获取图片数据
            inStream = conn.getInputStream();
            //得到图片的二进制数据
            return FileCopyUtils.copyToByteArray(inStream);
        } catch (Exception e) {
            log.error("下载人脸图片时出现异常", e);
        } finally {
            try {
                if (Objects.nonNull(inStream)) {
                    inStream.close();
                }
            } catch (IOException e) {
                log.error("关闭InputStream时出现异常", e);
            }
        }
        return new byte[0];
    }



    public static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }};

    /**
     * 设置不验证主机
     */
    public static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * 信任所有
     *
     * @param connection
     *
     * @return
     */
    public static SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
        SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldFactory;
    }
}
