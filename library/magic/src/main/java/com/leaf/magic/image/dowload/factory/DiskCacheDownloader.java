package com.leaf.magic.image.dowload.factory;

import android.content.Context;

import com.leaf.magic.image.cache.DiskManager;
import com.leaf.magic.image.dowload.ImageDownloadInfo;
import com.leaf.magic.image.dowload.ImageDownloader;

import java.io.InputStream;

/**
 * Created by hong on 2017/3/20.
 */

public class DiskCacheDownloader implements ImageDownloader {

    protected final Context context;

    public DiskCacheDownloader(Context context) {
        this.context = context;
    }

    @Override
    public InputStream getStream(ImageDownloadInfo imageDownloadInfo) {
        return DiskManager.with(context).getStream(imageDownloadInfo.getImageUrl());
    }

}
