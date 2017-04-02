package com.leaf.magic.request.factory;



import com.leaf.magic.request.Request;
import com.leaf.magic.request.Response;
import com.leaf.magic.utils.NetworkUitls;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by hong on 2017/3/17.
 */

public class JsonObjectRequest extends Request {

    private Response.Listener<JSONObject> mResponse;

    public JsonObjectRequest(Method method, String url, String mRequestBody, Response.Listener<JSONObject> mResponse, Response.ErrorResponse<String> mErrorResponse) {
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
            mResponse.onResponse(new JSONObject(result));
        } catch (JSONException e) {
            mErrorResponse.onErrorResponse("result is " + e.toString());
        }

    }

    public byte[] getBody() {
        try {
            return this.mRequestBody == null?null:this.mRequestBody.getBytes(this.getParamsEncoding());
        } catch (UnsupportedEncodingException var2) {
            return null;
        }
    }

}
