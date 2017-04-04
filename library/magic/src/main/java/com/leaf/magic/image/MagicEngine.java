package com.leaf.magic.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.leaf.magic.Magic;
import com.leaf.magic.image.cache.DiskManager;
import com.leaf.magic.image.cache.ImageCache;
import com.leaf.magic.image.dowload.BaseDecodeStream;
import com.leaf.magic.image.dowload.ImageDownloadInfo;
import com.leaf.magic.image.dowload.DownloadStream;
import com.leaf.magic.image.dowload.factory.DownloadFactory;
import com.leaf.magic.image.listener.ImageType;
import com.leaf.magic.image.listener.OnLoadListener;
import com.leaf.magic.image.listener.LoadForm;
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

        if (isExistedInMemory(imageDownloadInfo)) {
            return;
        }
        if (imageDownloadInfo.getImageAware() != null) {
            imageDownloadInfo.getImageAware().setImageResource(imageDownloadInfo.getLoadingImageRes());
            imageDownloadInfo.getImageAware().getWrappedView().setTag(imageDownloadInfo.getMemoryCacheKey());
        }
        magic.executeInThread(new RequestBitmapTask(getLock(imageDownloadInfo), imageDownloadInfo));
    }

    private boolean isExistedInMemory(ImageDownloadInfo imageDownloadInfo) {

        OnLoadListener mListener = imageDownloadInfo.getLoadListener();
        Bitmap btp = (Bitmap) mImageCache.get(imageDownloadInfo.getMemoryCacheKey());
        if (btp != null) {
            if (mListener != null) {
                if (DEBUG) {
                    Log.d(TAG, "bitmap from memory cache.");
                }
                mListener.onLoadSucessed(btp, imageDownloadInfo.getImageUrl());
            }
            if (imageDownloadInfo.getImageAware() != null) {
                imageDownloadInfo.getImageAware().setImageBitmap(btp);
                return true;
            }

        }
        return false;
    }

    private Lock getLock(ImageDownloadInfo imageDownloadInfo) {
        Lock lock = mLock.get(imageDownloadInfo.getMemoryCacheKey());
        if (lock == null) {
            lock = new ReentrantLock();
            mLock.put(imageDownloadInfo.getMemoryCacheKey(), lock);
        }
        return lock;
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
                if (DEBUG) {
                    Log.d(TAG, " waitIfPaused imageUrl= " + imageDownloadInfo.getImageUrl());
                }
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

    public void tryAsyncBitmap(ImageDownloadInfo imageDownloadInfo) {
        magic.executeInThread(new AsyncBitmapTask(imageDownloadInfo));
    }

    private class AsyncBitmapTask implements Runnable {
        private ImageDownloadInfo imageDownloadInfo;

        public AsyncBitmapTask(ImageDownloadInfo imageDownloadInfo) {
            this.imageDownloadInfo = imageDownloadInfo;
        }

        @Override
        public void run() {
            final Bitmap btm = tryLoadBitmap(imageDownloadInfo);
            final OnLoadListener mListener = imageDownloadInfo.getLoadListener();
            if (mHander == null) {
                mHander = new MyHandler(Looper.getMainLooper());
            }
            mHander.post(new Runnable() {
                @Override
                public void run() {
                    if (btm == null) {
                        if (mListener != null) {
                            mListener.onLoadFailed("get bitmap is null");
                        }
                    } else {
                        if (mListener != null) {
                            mListener.onLoadSucessed(btm, imageDownloadInfo.getImageUrl());
                        }
                    }
                }

            });

        }
    }

    private Bitmap tryLoadBitmap(ImageDownloadInfo imageDownloadInfo) {
        if (existsCache(imageDownloadInfo.getImageUrl())) {
            imageDownloadInfo.setLoadForm(LoadForm.DISC_CACHE);
            imageDownloadInfo.setImageType(ImageType.DISC_CACHE);
        }
        DownloadStream mImageDownloader = DownloadFactory.getDownloader(mContext, imageDownloadInfo.getImageType());
        BaseDecodeStream mDecodeStream = new BaseDecodeStream();
        return mDecodeStream.decodeStream(mContext, mImageDownloader, imageDownloadInfo);
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
