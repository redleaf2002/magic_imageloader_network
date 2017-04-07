package com.leaf.magic.request.factory;


import com.leaf.magic.request.Request;
import com.leaf.magic.request.Response;
import com.leaf.magic.utils.NetworkUitls;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by hong on 2017/3/17.
 */

public class StringRequest extends Request {

    private Response.Listener<String> mResponse;

    public StringRequest(Method method, String url, String mRequestBody, Response.Listener<String> mResponse, Response.ErrorResponse<String> mErrorResponse) {
        this.method = method;
        this.url = url;
        this.mRequestBody = mRequestBody;
        this.mResponse = mResponse;
        this.mErrorResponse = mErrorResponse;
    }

    @Override
    protected void parseResponse(InputStream inputStream) {
        String result = NetworkUitls.instream2String(inputStream);
        mResponse.onResponse(result);
    }

    public byte[] getBody() {
        try {
            return this.mRequestBody == null ? null : this.mRequestBody.getBytes(this.getParamsEncoding());
        } catch (UnsupportedEncodingException var2) {
            return null;
        }
    }

}
