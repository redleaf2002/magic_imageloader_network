package com.leaf.magic.request.factory;


import com.leaf.magic.request.Request;
import com.leaf.magic.request.Response;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by hong on 2017/3/17.
 */

public class InputStreamRequest extends Request {

    private Response.Listener<InputStream> mResponse;

    public InputStreamRequest(Method method, String url, String mRequestBody, Response.Listener<InputStream> mResponse, Response.ErrorResponse<String> mErrorResponse) {
        this.method = method;
        this.url = url;
        this.mRequestBody = mRequestBody;
        this.mResponse = mResponse;
        this.mErrorResponse = mErrorResponse;
    }

    @Override
    protected void parseResponse(InputStream inputStream) {
        mResponse.onResponse(inputStream);
    }

    public byte[] getBody() {
        try {
            return this.mRequestBody == null ? null : this.mRequestBody.getBytes(this.getParamsEncoding());
        } catch (UnsupportedEncodingException var2) {
            return null;
        }
    }

}
