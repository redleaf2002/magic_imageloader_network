package com.leaf.magic.image.dowload.factory;

import android.content.Context;

import com.leaf.magic.image.dowload.BaseDownloadStream;
import com.leaf.magic.image.dowload.DownloadStream;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hong on 2017/3/30.
 */

public class DownloadFactory {
    private static ConcurrentHashMap<String, DownloadStream> mDownloadMap = new ConcurrentHashMap<>();

    public static DownloadStream getDownloader(Context mContext, String key) {
        DownloadStream imageDownloader = mDownloadMap.get(key);
        if (imageDownloader == null) {
            imageDownloader = createDownloader(mContext, key);
            mDownloadMap.put(key, imageDownloader);
        }
        return imageDownloader;
    }

    public static void addDownloader(String key, DownloadStream imageDownloader) {
        mDownloadMap.put(key, imageDownloader);
    }

    private static DownloadStream createDownloader(Context mContext, String key) {
        return new BaseDownloadStream(mContext);
    }
}
