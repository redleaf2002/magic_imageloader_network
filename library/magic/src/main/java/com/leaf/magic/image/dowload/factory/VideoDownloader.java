package com.leaf.magic.image.dowload.factory;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;


import com.leaf.magic.image.dowload.ImageDownloadInfo;
import com.leaf.magic.image.dowload.ImageDownloader;
import com.leaf.magic.image.listener.LoadedFrom;
import com.leaf.magic.image.listener.Scheme;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hong on 2017/3/20.
 */

public class VideoDownloader implements ImageDownloader {

    protected final Context context;

    public VideoDownloader(Context context) {
        this.context = context;
    }

    @Override
    public InputStream getStream(ImageDownloadInfo imageDownloadInfo) {
        String imageUrl = imageDownloadInfo.getImageUrl();
        try {
            switch (Scheme.getUrl(imageUrl)) {
            case VIDEO:
                if (imageDownloadInfo.getExtraInfo() == 0) {
                    imageDownloadInfo.setExtraInfo(MediaStore.Images.Thumbnails.MINI_KIND);
                }
                if (imageDownloadInfo.getExtraInfo() == MediaStore.Images.Thumbnails.FULL_SCREEN_KIND) {
                    imageDownloadInfo.setLoadedFrom(LoadedFrom.NETWORK);
                } else {
                    imageDownloadInfo.setLoadedFrom(LoadedFrom.FILE_LOCAL);
                }
                return getStreamFromVideo(imageUrl, imageDownloadInfo.getExtraInfo());
            }
        } catch (IOException e) {
            Log.d("error", "e:" + e.toString());
        }
        return null;
    }

    protected InputStream getStreamFromVideo(String imageUri, int kind) throws IOException {
        imageUri = Scheme.VIDEO.crop(imageUri);
        try {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(imageUri,
                    kind);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (Exception e) {
            Log.e("error", "e: " + e.toString());
        }
        return null;
    }


}
