package com.leaf.magic.image.dowload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.leaf.magic.Magic;
import com.leaf.magic.image.cache.DiskManager;
import com.leaf.magic.image.listener.LoadForm;

import java.io.InputStream;

/**
 * Created by hong on 2017/3/20.
 */

public class BaseDecodeStream implements DecodeStream {
    private static final boolean DEBUG = Magic.DEBUG;
    private static final String TAG = Magic.TAG;

    /*only image from network needs to have disk cache
    * */
    @Override
    public Bitmap decodeStream(Context mContext, DownloadStream downloader, ImageDownloadInfo imageDownloadInfo) {
        InputStream in = downloader.getStream(imageDownloadInfo);
        if (in == null) {
            return null;
        }
        if (LoadForm.NETWORK.equals(imageDownloadInfo.getLoadForm()) || imageDownloadInfo.isEnableCache()) {
            DiskManager.with(mContext).backup(imageDownloadInfo.getImageUrl(), in);
            in = DiskManager.with(mContext).getStream(imageDownloadInfo.getImageUrl());
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in, null, options);
        options.inSampleSize = imageDownloadInfo.getSampleSize(options.outWidth, options.outHeight);
        options.inJustDecodeBounds = false;
        if (LoadForm.NETWORK.equals(imageDownloadInfo.getLoadForm())) {
            in = DiskManager.with(mContext).getStream(imageDownloadInfo.getImageUrl());
        } else {
            in = downloader.getStream(imageDownloadInfo);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
        if (!imageDownloadInfo.isEnableCache()) {
            DiskManager.with(mContext).deleteCache(imageDownloadInfo.getImageUrl());
        }
        return bitmap;

    }

}
