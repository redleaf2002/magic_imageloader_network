package com.leaf.magic.request;

/**
 * Created by Administrator on 2017/3/17.
 */

public class Response {
    public interface Listener<T> {
        void onResponse(T response);
    }

    public interface ErrorResponse<T> {
        void onErrorResponse(String error);
    }
}
