package com.leaf.magic.image.dowload.factory;

import android.content.Context;

import com.leaf.magic.image.ImageRequest;
import com.leaf.magic.image.MagicEngine;
import com.leaf.magic.image.dowload.BaseImageDownloader;
import com.leaf.magic.image.dowload.ImageDownloader;
import com.leaf.magic.image.listener.ImageType;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hong on 2017/3/30.
 */

public class DownloadUtils {
    private static ConcurrentHashMap<String, ImageDownloader> mDownloadMap = new ConcurrentHashMap<>();

    public static ImageDownloader getDownloader(Context mContext, String key) {
        ImageDownloader imageDownloader = mDownloadMap.get(key);
        if (imageDownloader == null) {
            imageDownloader = createDownloader(mContext, key);
            mDownloadMap.put(key, imageDownloader);
        }
        return imageDownloader;
    }

    public static void addDownloader(String key, ImageDownloader imageDownloader) {
        mDownloadMap.put(key, imageDownloader);
    }

    private static ImageDownloader createDownloader(Context mContext, String key) {
        if (ImageType.ASSETS.equals(key)) {
            return new AssetDownloader(mContext);
        } else if (ImageType.DIST_CACHE.equals(key)) {
            return new DiskCacheDownloader(mContext);
        } else if (ImageType.VIDEO.equals(key)) {
            return new VideoDownloader(mContext);
        }
        return new BaseImageDownloader(mContext);
    }
}
