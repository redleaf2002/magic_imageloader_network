package com.leaf.magic;

import android.content.Context;

import com.leaf.magic.image.ImageRequest;
import com.leaf.magic.image.MagicEngine;
import com.leaf.magic.image.cache.DiskManager;
import com.leaf.magic.image.dowload.ImageDownloader;
import com.leaf.magic.image.dowload.factory.DownloadUtils;
import com.leaf.magic.request.Request;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Hong
 */

public class Magic {
    public static final boolean DEBUG = false;
    public static final String TAG = "ImageDownloader";
    private static Magic mImageLoader = null;
    private final static Object object = new Object();
    private ExecutorService mExecutor;
    private Context mContext;
    public MagicEngine magicEngine;

    private Magic(Context mContext) {
        this.mContext = mContext.getApplicationContext();
        mExecutor = Executors.newCachedThreadPool();
        magicEngine = new MagicEngine(mContext, this);
    }

    public static Magic with(Context mContext) {
        if (mImageLoader == null) {
            synchronized (object) {
                if (mImageLoader == null) {
                    mImageLoader = new Magic(mContext);
                }
            }
        }
        return mImageLoader;
    }

    public ImageRequest loadImage(String imageUrl) {
        return new ImageRequest(mContext, this, imageUrl, 0);
    }

    public ImageRequest loadImage(int resId) {
        return new ImageRequest(mContext, this, null, resId);
    }

    public InputStream getStreamFromDisk(String url) {
        return DiskManager.with(mContext).getStream(url);
    }

    public void addDownloaderType(String key, ImageDownloader imageDownloader) {
        DownloadUtils.addDownloader(key, imageDownloader);
    }

    public void addRequest(Request mRequest) {
        executeInThread(mRequest);
    }

    public void executeInThread(Runnable runnable) {
        if (mExecutor == null) {
            mExecutor = Executors.newCachedThreadPool();
        }
        mExecutor.execute(runnable);
    }


}
