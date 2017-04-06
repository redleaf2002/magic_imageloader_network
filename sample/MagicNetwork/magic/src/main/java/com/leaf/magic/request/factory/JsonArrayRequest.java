package com.leaf.magic.request.factory;


import com.leaf.magic.request.Request;
import com.leaf.magic.request.Response;
import com.leaf.magic.utils.NetworkUitls;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by hong on 2017/3/17.
 */

public class JsonArrayRequest extends Request {

    private Response.Listener<JSONArray> mResponse;

    public JsonArrayRequest(Method method, String url, String mRequestBody, Response.Listener<JSONArray> mResponse, Response.ErrorResponse<String> mErrorResponse) {
        this.method = method;
        this.url = url;
        this.mRequestBody = mRequestBody;
        this.mResponse = mResponse;
        this.mErrorResponse = mErrorResponse;
    }

    @Override
    protected void parseResponse(InputStream inputStream) {
        String result = NetworkUitls.instream2String(inputStream);
        try {
            mResponse.onResponse(new JSONArray(result));
        } catch (JSONException e) {
            mErrorResponse.onErrorResponse("result is " + e.toString());
        }

    }

    public byte[] getBody() {
        try {
            return this.mRequestBody == null ? null : this.mRequestBody.getBytes(this.getParamsEncoding());
        } catch (UnsupportedEncodingException var2) {
            return null;
        }
    }

}
