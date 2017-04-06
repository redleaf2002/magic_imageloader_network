package com.leaf.magic.image.cache;

import android.support.v4.util.LruCache;

/**
 * @author: Hong
 * @date: 2017/3/17 21:39
 */

public class ImageCache extends LruCache {
    private static ImageCache mImageCache;

    public ImageCache(int maxSize) {
        super(maxSize);
    }

    public static ImageCache getInstance(int maxSize) {
        if (mImageCache == null) {
            mImageCache = new ImageCache(maxSize);
        }
        return mImageCache;
    }
}
