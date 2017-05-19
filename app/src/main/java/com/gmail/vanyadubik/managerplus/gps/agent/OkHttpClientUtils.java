//package com.gmail.vanyadubik.managerplus.gps.agent;
//
//
//import android.content.Context;
//import com.google.common.net.HttpHeaders;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import java.security.SecureRandom;
//import java.security.cert.X509Certificate;
//import java.util.Locale;
//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.SSLContext;
//import javax.net.ssl.SSLSession;
//import javax.net.ssl.SSLSocketFactory;
//import javax.net.ssl.TrustManager;
//import javax.net.ssl.X509TrustManager;
//import org.apache.commons.net.imap.IMAPSClient;
//import com.gmail.vanyadubik.managerplus.gps.agent.MdmService;
//
//public class OkHttpClientUtils {
//
//    /* renamed from: ru.agentplus.utils.OkHttpClientUtils.1 */
//    static class C04551 implements X509TrustManager {
//        C04551() {
//        }
//
//        public void checkClientTrusted(X509Certificate[] chain, String authType) {
//        }
//
//        public void checkServerTrusted(X509Certificate[] chain, String authType) {
//        }
//
//        public X509Certificate[] getAcceptedIssuers() {
//            return null;
//        }
//    }
//
//    /* renamed from: ru.agentplus.utils.OkHttpClientUtils.2 */
//    static class C04562 implements HostnameVerifier {
//        C04562() {
//        }
//
//        public boolean verify(String hostname, SSLSession session) {
//            return true;
//        }
//    }
//
//    public static Request getSignedRequest(Context context, Request request) {
//        StringBuilder data = new StringBuilder();
//        data.append(request.method());
//        data.append("\n");
//        data.append(request.url().getPath());
//        data.append("\n");
//        String contentType = request.header(HttpHeaders.CONTENT_TYPE);
//        if (!(request.body() == null || request.body().contentType() == null)) {
//            contentType = request.body().contentType().toString();
//        }
//        if (contentType != null) {
//            data.append(contentType);
//        }
//        data.append("\n");
//        String date = request.header(HttpHeaders.DATE);
//        if (date != null) {
//            data.append(date);
//        }
//        data.append("\n");
//        String contentSHA256 = request.header("X-Content-SHA256");
//        if (contentSHA256 != null) {
//            data.append(contentSHA256);
//        }
//        String signature = MdmService.signData(context, data.toString());
//        String deviceId = MdmService.getDeviceId(context);
//        return request.newBuilder().addHeader(HttpHeaders.AUTHORIZATION, String.format(Locale.getDefault(), "device-signature %s:%s", new Object[]{deviceId, signature})).build();
//    }
//
//    public static OkHttpClient getUnsafeOkHttpClient() {
//        try {
//            TrustManager[] trustAllCerts = new TrustManager[]{new C04551()};
//            SSLContext sslContext = SSLContext.getInstance(IMAPSClient.DEFAULT_PROTOCOL);
//            sslContext.init(null, trustAllCerts, new SecureRandom());
//            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//            OkHttpClient client = new OkHttpClient();
//            client.setFollowSslRedirects(true);
//            client.setSslSocketFactory(sslSocketFactory);
//            client.setHostnameVerifier(new C04562());
//            return client;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}