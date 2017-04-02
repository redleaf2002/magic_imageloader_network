package com.leaf.magic.image.dowload.factory;

import android.content.Context;
import android.util.Log;


import com.leaf.magic.image.dowload.ImageDownloadInfo;
import com.leaf.magic.image.dowload.ImageDownloader;
import com.leaf.magic.image.listener.LoadedFrom;
import com.leaf.magic.image.listener.Scheme;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hong on 2017/3/20.
 */

public class AssetDownloader implements ImageDownloader {

    protected final Context context;

    public AssetDownloader(Context context) {
        this.context = context;
    }

    @Override
    public InputStream getStream(ImageDownloadInfo imageDownloadInfo) {
        String imageUrl = imageDownloadInfo.getImageUrl();
        try {
            switch (Scheme.getUrl(imageUrl)) {
            case ASSETS:
                imageDownloadInfo.setLoadedFrom(LoadedFrom.FILE_LOCAL);
                return getStreamFromAssets(imageUrl, null);
            }
        } catch (IOException e) {
            Log.d("error", "e:" + e.toString());
        }
        return null;
    }

    protected InputStream getStreamFromAssets(String imageUri, Object extra) throws IOException {
        String filePath = Scheme.ASSETS.crop(imageUri);
        return this.context.getAssets().open(filePath);
    }


}
