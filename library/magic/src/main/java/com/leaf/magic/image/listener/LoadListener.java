package com.leaf.magic.image.listener;

import android.graphics.Bitmap;

/**
 * Created by hong on 2016/7/12.
 */
public interface LoadListener {
    void onLoadStarted(String url);

    void onLoadSucessed(Bitmap bmp, String url);

    void onLoadFailed(String error);

}
