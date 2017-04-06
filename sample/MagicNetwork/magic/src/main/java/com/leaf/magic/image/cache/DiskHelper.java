package com.leaf.magic.image.cache;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author: Hong
 * @date: 2017/3/24 12:41
 * * this class provides 4 methods for add(backup) get delete
 */

public class DiskHelper {

    private final static int MAX_CACHE = 300 * 1024 * 1024;// 300M
    private static DiskHelper mDiskHelper;
    private Context context;
    private final Object mDiskCacheLock = new Object();

    private DiskHelper(Context context) {
        this.context = context;
    }

    private static DiskLruCache mDiskLruCache;

    public static DiskHelper getInstance(Context context, String cacheDir) {
        if (mDiskHelper == null) {
            mDiskHelper = new DiskHelper(context);
        }
        mDiskHelper.mDiskLruCache = openDiskLru(context, cacheDir);
        return mDiskHelper;
    }

    private static DiskLruCache openDiskLru(Context context, String uniqueName) {
        DiskLruCache mDiskLruCache = null;
        try {
            File cacheDir = DiskUtils.getDiskCacheDir(context, uniqueName);
            mDiskLruCache = DiskLruCache.open(cacheDir, DiskUtils.getAppVersion(context, false), 1, MAX_CACHE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mDiskLruCache;
    }

    public InputStream getCache(String cacheUrl) {
        InputStream is = null;
        String key = DiskUtils.hashKeyForDisk(cacheUrl);
        DiskLruCache.Snapshot snapShot;
        try {
            snapShot = mDiskLruCache.get(key);
            if (snapShot != null) {
                is = snapShot.getInputStream(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }

    public String getStringCache(String cacheUrl) {
        String content = null;
        String key = DiskUtils.hashKeyForDisk(cacheUrl);
        DiskLruCache.Snapshot snapShot;
        try {
            snapShot = mDiskLruCache.get(key);
            if (snapShot != null) {
                content = snapShot.getString(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public void writeCache(final File file, final String cacheUrl) {
        InputStream in;
        try {
            in = new FileInputStream(file);
            if (in != null) {
                writeCache(in, cacheUrl);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeCache(final InputStream in, final String cacheUrl) {
        if (TextUtils.isEmpty(cacheUrl)) {
            return;
        }
        try {
            String key = DiskUtils.hashKeyForDisk(cacheUrl);
            if (mDiskHelper.mDiskLruCache == null) {
                return;
            }
            DiskLruCache.Editor editor = mDiskHelper.mDiskLruCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
                editor.commit();
            }
            mDiskLruCache.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteCacheFile(String key) {
        try {
            key = DiskUtils.hashKeyForDisk(key);
            return this.mDiskLruCache.remove(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void clearAllCache() {
        synchronized (this.mDiskCacheLock) {
            if (this.mDiskLruCache != null && !this.mDiskLruCache.isClosed()) {
                try {
                    this.mDiskLruCache.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.mDiskLruCache = null;
            }

        }
    }


}
