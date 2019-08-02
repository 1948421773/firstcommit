package com.musicdo.musicshop.util;

import android.content.Context;
import android.text.StaticLayout;

import com.musicdo.musicshop.R;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;

/**
 * 描述:
 * 作者：haiming on 2017/8/22 09:40
 * 邮箱：1948421773@qq.com
 * 版 本 ：1.0
 * 备 注 ：
 */

public class MyHttpClient {

    public static HttpClient getNewHttpClient(Context context) {
        InputStream keyStream = null;
        try {
            /*KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());
            trustStore.load(null, null);*/
            KeyStore trustKeyStore= KeyStore.getInstance(KeyStore.getDefaultType());// 访问Java密钥库，JKS是keytool创建的Java密钥库
//            InputStream keyStream = context.getAssets().open("key.bks");//打开证书文件（.jks格式）
            keyStream = context.getResources().openRawResource(R.raw.key);//打开证书文件（.jks格式）
            char keyStorePass[]="musicshop".toCharArray();  //证书密码
            trustKeyStore.load(keyStream,keyStorePass);

          /*  trustKeyStore.load(null);
            CertificateFactory certificateFactory = CertificateFactory
                    .getInstance("X.509");
            String certificateAlias = Integer.toString(2);
            trustKeyStore.setCertificateEntry(certificateAlias,
                    certificateFactory.generateCertificate(keyStream));
            keyStream.close();*/

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustKeyStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }finally {
            if (keyStream != null) {
                try {
                    keyStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }}

}
