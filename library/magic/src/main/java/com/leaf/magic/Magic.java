package com.leaf.magic;

import android.content.Context;

import com.leaf.magic.image.ImageRequest;
import com.leaf.magic.image.MagicEngine;
import com.leaf.magic.image.cache.DiskManager;
import com.leaf.magic.image.dowload.DownloadStream;
import com.leaf.magic.image.dowload.factory.DownloadFactory;
import com.leaf.magic.image.listener.ImageType;
import com.leaf.magic.request.Request;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: Hong
 */

public class Magic {
    public static final boolean DEBUG = true;
    public static final String TAG = "Magic";
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

    public ImageRequest loadImage(String imageUrl, String imageType) {
        return new ImageRequest(mContext, this, imageUrl, imageType);
    }

    public ImageRequest loadImage(int resId) {
        return new ImageRequest(mContext, this, String.valueOf(resId), ImageType.DRAWABLE);
    }

    public InputStream getStreamFromDisk(String url) {
        return DiskManager.with(mContext).getStream(url);
    }

    public void addStreamType(String key, DownloadStream imageDownloader) {
        DownloadFactory.addDownloader(key, imageDownloader);
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
