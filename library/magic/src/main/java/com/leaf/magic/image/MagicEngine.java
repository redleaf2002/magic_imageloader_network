package com.leaf.magic.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;


import com.leaf.magic.Magic;
import com.leaf.magic.image.cache.DiskManager;
import com.leaf.magic.image.cache.ImageCache;
import com.leaf.magic.image.dowload.BaseDecodeStream;
import com.leaf.magic.image.dowload.ImageDownloadInfo;
import com.leaf.magic.image.dowload.ImageDownloader;
import com.leaf.magic.image.dowload.factory.DownloadUtils;
import com.leaf.magic.image.listener.ImageType;
import com.leaf.magic.image.listener.LoadListener;
import com.leaf.magic.image.listener.LoadedFrom;
import com.leaf.magic.image.task.DisplayBitmapTask;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hong
 */

public class MagicEngine {
    public static final boolean DEBUG = Magic.DEBUG;
    public static final String TAG = Magic.TAG;
    private final AtomicBoolean paused = new AtomicBoolean(false);
    private final Object pauseLock = new Object();
    private ImageCache mImageCache = null;
    private MyHandler mHander;
    private Context mContext;
    private ConcurrentHashMap<String, Lock> mLock = new ConcurrentHashMap<>();
    private Magic magic;

    public MagicEngine(Context mContext, Magic magic) {
        this.mContext = mContext;
        this.magic = magic;
        int mTotalSize = (int) Runtime.getRuntime().totalMemory();
        mImageCache = ImageCache.getInstance(mTotalSize / 16);
    }

    public void tryDisplayImage(ImageDownloadInfo imageDownloadInfo) {
        if (DEBUG) {
            Log.d(TAG, "displayImage url:" + imageDownloadInfo.getImageUrl());
        }
        LoadListener mListener = imageDownloadInfo.getLoadListener();
        if (mListener != null) {
            mListener.onLoadStarted(imageDownloadInfo.getImageUrl());
        }
        if (TextUtils.isEmpty(imageDownloadInfo.getImageUrl()) || imageDownloadInfo.getImageAware() == null) {
            if (mListener != null) {
                mListener.onLoadFailed("url is empty");
            }
            return;
        }
        Bitmap btp = (Bitmap) mImageCache.get(imageDownloadInfo.getImageUrl());
        if (btp != null) {
            if (DEBUG) {
                Log.d(TAG, "bitmap from memory cache.");
            }
            if (mListener != null) {
                mListener.onLoadSucessed(btp, imageDownloadInfo.getImageUrl());
            }
            imageDownloadInfo.getImageAware().setImageBitmap(btp);
            return;
        }
        imageDownloadInfo.getImageAware().setImageResource(imageDownloadInfo.getLoadingImageRes());
        imageDownloadInfo.getImageAware().getWrappedView().setTag(imageDownloadInfo.getMemoryCacheKey());
        Lock lock = mLock.get(imageDownloadInfo.getMemoryCacheKey());
        if (lock == null) {
            lock = new ReentrantLock();
            mLock.put(imageDownloadInfo.getMemoryCacheKey(), lock);
        }
        magic.executeInThread(new RequestBitmapTask(lock, imageDownloadInfo));
    }

    private class RequestBitmapTask implements Runnable {
        private Lock lock;
        private ImageDownloadInfo imageDownloadInfo;

        public RequestBitmapTask(Lock lock, ImageDownloadInfo imageDownloadInfo) {
            this.lock = lock;
            this.imageDownloadInfo = imageDownloadInfo;

        }

        @Override
        public void run() {
            if (waitIfPaused(imageDownloadInfo)) {
                Log.d(TAG, "not execute ");
                return;
            }
            lock.lock();
            try {
                Bitmap mBtp = (Bitmap) mImageCache.get(imageDownloadInfo.getMemoryCacheKey());
                if (DEBUG && mBtp != null) {
                    Log.d(TAG, "bitmap from memory cache!");
                }
                if (mBtp == null) {
                    mBtp = tryLoadBitmap(imageDownloadInfo);
                    //save the bitmap to the memory cache
                    if (mBtp != null) {
                        mImageCache.put(imageDownloadInfo.getMemoryCacheKey(), mBtp);
                    }
                }
                displayImageTask(mBtp, imageDownloadInfo);
            } finally {
                lock.unlock();
            }
        }
    }

    private void displayImageTask(Bitmap bitmap, ImageDownloadInfo imageDownloadInfo) {
        DisplayBitmapTask task = new DisplayBitmapTask(bitmap, imageDownloadInfo);
        if (mHander == null) {
            mHander = new MyHandler(Looper.getMainLooper());
        }
        mHander.post(task);
    }

    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }
    }

    private Bitmap tryLoadBitmap(ImageDownloadInfo imageDownloadInfo) {
        if (existsCache(imageDownloadInfo.getImageUrl())) {
            imageDownloadInfo.setLoadedFrom(LoadedFrom.DISC_CACHE);
            imageDownloadInfo.setDownloadType(ImageType.DIST_CACHE);
        }
        ImageDownloader mImageDownloader = DownloadUtils.getDownloader(mContext, imageDownloadInfo.getDownloadType());
        BaseDecodeStream mImpl = new BaseDecodeStream();
        return mImpl.decodeStream(mContext, mImageDownloader, imageDownloadInfo);
    }

    private boolean existsCache(String url) {
        return DiskManager.with(mContext).existDiskCache(url);
    }

    private boolean waitIfPaused(ImageDownloadInfo imageDownloadInfo) {
        AtomicBoolean pause = getPause();
        if (pause.get()) {
            synchronized (getPauseLock()) {
                if (pause.get()) {
                    try {
                        getPauseLock().wait();
                    } catch (InterruptedException e) {
                        return true;
                    }
                }
            }
        }
        return isTaskNotActual(imageDownloadInfo);
    }

    public static boolean isTaskNotActual(ImageDownloadInfo imageDownloadInfo) {
        return imageDownloadInfo.getImageAware().isCollected() || isImageViewReused(imageDownloadInfo);
    }

    private static boolean isImageViewReused(ImageDownloadInfo imageDownloadInfo) {
        if (imageDownloadInfo != null && imageDownloadInfo.getImageAware() != null && imageDownloadInfo.getImageAware().getWrappedView().getTag().equals(imageDownloadInfo.getMemoryCacheKey())) {
            return false;
        }
        return true;
    }


    /**
     * Already running tasks are not paused.
     */
    public void pause() {
        paused.set(true);
    }

    /**
     * Resumes engine work. Paused tasks will continue its work.
     */
    public void resume() {
        paused.set(false);
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
    }

    public AtomicBoolean getPause() {
        return paused;
    }

    public Object getPauseLock() {
        return pauseLock;
    }


}
