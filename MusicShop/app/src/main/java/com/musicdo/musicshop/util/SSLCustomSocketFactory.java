package com.musicdo.musicshop.util;

import android.content.Context;
import android.util.Log;

import com.musicdo.musicshop.R;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.InputStream;
import java.security.KeyStore;

/**
 * Created by Administrator on 2017/9/6.
 */

public class SSLCustomSocketFactory {
    private static final String TAG = "SSLCustomSocketFactory";

    private static final String KEY_PASS = "musicshop";

    public SSLCustomSocketFactory(KeyStore trustStore) throws Throwable {
        super();
    }

    public static SSLCustomSocketFactory getSocketFactory(Context context) {
        try {
            InputStream ins = context.getResources().openRawResource(R.raw.key);

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try {
                trustStore.load(ins, KEY_PASS.toCharArray());
            }
            finally {
                ins.close();
            }
            SSLCustomSocketFactory factory =new SSLCustomSocketFactory(trustStore);/*trustStore*/
            return factory;
        } catch (Throwable e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
