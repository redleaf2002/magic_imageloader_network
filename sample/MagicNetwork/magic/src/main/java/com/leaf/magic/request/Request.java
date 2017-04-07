package com.leaf.magic.request;

import android.util.Log;


import com.leaf.magic.Magic;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by hong on 2017/3/17.
 */

public abstract class Request implements Runnable {
    protected static final boolean DEBUG = Magic.DEBUG;
    protected static final String TAG = "Request";
    protected Method method;
    protected String url;
    protected String mRequestBody;
    protected Response.ErrorResponse<String> mErrorResponse;

    protected Map<String, String> getParams() {
        return null;
    }

    @Override
    public void run() {
        try {
            URL mUrl = new URL(url);
            HttpURLConnection mConn = (HttpURLConnection) mUrl.openConnection();
            mConn.setConnectTimeout(1500);
            mConn.setRequestProperty("Content-Type", "application/json");
            addHeaders(mConn);
            switch (method) {
                case GET:
                    mConn.setRequestMethod("GET");
                    break;
                case POST:
                    mConn.setRequestMethod("POST");
                    mConn.setDoOutput(true);
                    OutputStream out = mConn.getOutputStream();
                    out.write(getBody());
                    out.flush();
                    out.close();
                    break;
                default:
                    return;
            }
            int code = mConn.getResponseCode();
            if (code == 200) {
                parseResponse(mConn.getInputStream());
            } else {
                mErrorResponse.onErrorResponse("responseCode = " + String.valueOf(code));
            }
        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "e: " + e.toString());
                e.printStackTrace();
            }
            mErrorResponse.onErrorResponse("response = " + e.toString());
        }
    }

    private void addHeaders(HttpURLConnection mConn) {
        Map<String, String> mHeaderMap = getHeaders();
        Iterator<Map.Entry<String, String>> mIterator = mHeaderMap.entrySet().iterator();
        while (mIterator.hasNext()) {
            Map.Entry<String, String> mEntry = mIterator.next();
            String key = mEntry.getKey();
            String value = mEntry.getValue();
            mConn.setRequestProperty(key, value);
        }
    }

    protected abstract void parseResponse(InputStream inputStream);

    protected Map<String, String> getHeaders() {
        return Collections.EMPTY_MAP;
    }


    public enum Method {
        GET, POST
    }

    public Method getMethod() {
        return this.method;
    }

    public byte[] getBody() {
        Map params = this.getParams();
        return params != null && params.size() > 0 ? this.encodeParameters(params, this.getParamsEncoding()) : null;
    }

    private byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();

        try {
            Iterator var5 = params.entrySet().iterator();

            while (var5.hasNext()) {
                Map.Entry uee = (Map.Entry) var5.next();
                encodedParams.append(URLEncoder.encode((String) uee.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode((String) uee.getValue(), paramsEncoding));
                encodedParams.append('&');
            }

            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException var6) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, var6);
        }
    }

    protected String getParamsEncoding() {
        return "UTF-8";
    }


}
