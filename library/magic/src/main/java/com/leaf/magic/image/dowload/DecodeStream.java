package com.leaf.magic.image.dowload;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by hong on 2017/3/20.
 */

public interface DecodeStream {
    Bitmap decodeStream(Context mContext, ImageDownloader downloader, ImageDownloadInfo imageDownloadInfo);
}
