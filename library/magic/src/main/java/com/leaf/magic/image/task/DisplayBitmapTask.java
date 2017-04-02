package com.leaf.magic.image.task;

import android.graphics.Bitmap;

import com.leaf.magic.image.MagicEngine;
import com.leaf.magic.image.aware.ImageAware;
import com.leaf.magic.image.dowload.ImageDownloadInfo;
import com.leaf.magic.image.listener.LoadListener;

/**
 * @author: Hong
 * @date: 2017/3/17 21:41
 */

public class DisplayBitmapTask implements Runnable {
    private ImageAware mImageAware;
    private Bitmap bitmap;
    private LoadListener mLoadListener;
    private ImageDownloadInfo imageDownloadInfo;

    public DisplayBitmapTask(Bitmap bitmap, ImageDownloadInfo imageDownloadInfo) {
        this.mImageAware = imageDownloadInfo.getImageAware();
        this.bitmap = bitmap;
        this.mLoadListener = imageDownloadInfo.getLoadListener();
        this.imageDownloadInfo = imageDownloadInfo;
    }

    @Override
    public void run() {
        if (bitmap != null && mImageAware.getWrappedView() != null) {
            if (!MagicEngine.isTaskNotActual(imageDownloadInfo)) {
                mImageAware.setImageBitmap(bitmap);
                if (mLoadListener != null) {
                    mLoadListener.onLoadSucessed(bitmap, imageDownloadInfo.getImageUrl());
                }
            } else {
                if (mLoadListener != null) {
                    mLoadListener.onLoadFailed("imageview reused or collected.");
                }
            }
        } else {
            if (mLoadListener != null) {
                mLoadListener.onLoadFailed("bitmap is null.");
            }
        }
    }

}
