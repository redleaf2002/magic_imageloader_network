package com.leaf.magic.image.cache;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.InputStream;

public class DiskManager {

    /*  for image and otherContent ex:json*/
    private final static String IMAGE_CACHE = "imagecache";
    private final static String CONTENT_CACHE = "contentcache";
    private static DiskManager mDiskManager;
    private Context mContext;
    private static int cacheType = 0;

    private DiskManager(Context mContext) {
        this.mContext = mContext;
    }

    public static DiskManager with(Context mContext) {
        return with(mContext, 0);
    }

    public static DiskManager with(Context mContext, int cacheType) {
        if (mDiskManager == null) {
            mDiskManager = new DiskManager(mContext);
        }
        return mDiskManager;
    }

    public void backup(String key, File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return;
        }
        DiskHelper.getInstance(mContext, getCacheDir()).writeCache(file, key);
    }

    public void backup(String key, InputStream in) {
        if (TextUtils.isEmpty(key) || in == null) {
            return;
        }
        DiskHelper.getInstance(mContext, getCacheDir()).writeCache(in, key);
    }

    public InputStream getStream(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        return DiskHelper.getInstance(mContext, getCacheDir()).getCache(key);
    }

    public boolean existDiskCache(String key) {
        return getStream(key) != null;
    }

    public String getString(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        return DiskHelper.getInstance(mContext, getCacheDir()).getStringCache(key);
    }

    public boolean deleteCache(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        return DiskHelper.getInstance(mContext, getCacheDir()).deleteCacheFile(key);
    }

    public void clear() {
        DiskHelper.getInstance(mContext, getCacheDir()).clearAllCache();
    }

    private String getCacheDir() {
        if (cacheType == 0) {
            return IMAGE_CACHE;
        }
        return CONTENT_CACHE;
    }

}
